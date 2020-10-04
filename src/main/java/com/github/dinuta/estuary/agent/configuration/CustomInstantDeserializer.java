package com.github.dinuta.estuary.agent.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.datatype.threetenbp.DateTimeUtils;
import com.fasterxml.jackson.datatype.threetenbp.DecimalUtils;
import com.fasterxml.jackson.datatype.threetenbp.deser.ThreeTenDateTimeDeserializerBase;
import com.fasterxml.jackson.datatype.threetenbp.function.BiFunction;
import com.fasterxml.jackson.datatype.threetenbp.function.Function;
import org.threeten.bp.*;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.Temporal;
import org.threeten.bp.temporal.TemporalAccessor;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Deserializer for ThreeTen temporal {@link Instant}s, {@link OffsetDateTime}, and {@link ZonedDateTime}s.
 * Adapted from the jackson threetenbp InstantDeserializer to add support for deserializing rfc822 format.
 *
 * @author Nick Williams
 */
public class CustomInstantDeserializer<T extends Temporal>
        extends ThreeTenDateTimeDeserializerBase<T> {
  public static final CustomInstantDeserializer<Instant> INSTANT = new CustomInstantDeserializer<Instant>(
          Instant.class, DateTimeFormatter.ISO_INSTANT,
          temporalAccessor -> Instant.from(temporalAccessor),
          a -> Instant.ofEpochMilli(a.value),
          a -> Instant.ofEpochSecond(a.integer, a.fraction),
          null
  );
  public static final CustomInstantDeserializer<OffsetDateTime> OFFSET_DATE_TIME = new CustomInstantDeserializer<OffsetDateTime>(
          OffsetDateTime.class, DateTimeFormatter.ISO_OFFSET_DATE_TIME,
          temporalAccessor -> OffsetDateTime.from(temporalAccessor),
          a -> OffsetDateTime.ofInstant(Instant.ofEpochMilli(a.value), a.zoneId),
          a -> OffsetDateTime.ofInstant(Instant.ofEpochSecond(a.integer, a.fraction), a.zoneId),
          (d, z) -> d.withOffsetSameInstant(z.getRules().getOffset(d.toLocalDateTime()))
  );
  public static final CustomInstantDeserializer<ZonedDateTime> ZONED_DATE_TIME = new CustomInstantDeserializer<ZonedDateTime>(
          ZonedDateTime.class, DateTimeFormatter.ISO_ZONED_DATE_TIME,
          temporalAccessor -> ZonedDateTime.from(temporalAccessor),
          a -> ZonedDateTime.ofInstant(Instant.ofEpochMilli(a.value), a.zoneId),
          a -> ZonedDateTime.ofInstant(Instant.ofEpochSecond(a.integer, a.fraction), a.zoneId),
          (zonedDateTime, zoneId) -> zonedDateTime.withZoneSameInstant(zoneId)
  );
  private static final long serialVersionUID = 1L;
  protected final Function<FromIntegerArguments, T> fromMilliseconds;

  protected final Function<FromDecimalArguments, T> fromNanoseconds;

  protected final Function<TemporalAccessor, T> parsedToValue;

  protected final BiFunction<T, ZoneId, T> adjust;

  protected CustomInstantDeserializer(Class<T> supportedType,
                                      DateTimeFormatter parser,
                                      Function<TemporalAccessor, T> parsedToValue,
                                      Function<FromIntegerArguments, T> fromMilliseconds,
                                      Function<FromDecimalArguments, T> fromNanoseconds,
                                      BiFunction<T, ZoneId, T> adjust) {
    super(supportedType, parser);
    this.parsedToValue = parsedToValue;
    this.fromMilliseconds = fromMilliseconds;
    this.fromNanoseconds = fromNanoseconds;
    this.adjust = adjust == null ? new BiFunction<T, ZoneId, T>() {
      @Override
      public T apply(T t, ZoneId zoneId) {
        return t;
      }
    } : adjust;
  }

  @SuppressWarnings("unchecked")
  protected CustomInstantDeserializer(CustomInstantDeserializer<T> base, DateTimeFormatter f) {
    super((Class<T>) base.handledType(), f);
    parsedToValue = base.parsedToValue;
    fromMilliseconds = base.fromMilliseconds;
    fromNanoseconds = base.fromNanoseconds;
    adjust = base.adjust;
  }

  @Override
  protected JsonDeserializer<T> withDateFormat(DateTimeFormatter dtf) {
    if (dtf == _formatter) {
      return this;
    }
    return new CustomInstantDeserializer<T>(this, dtf);
  }

  @Override
  public T deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    //NOTE: Timestamps contain no timezone info, and are always in configured TZ. Only
    //string values have to be adjusted to the configured TZ.
    switch (parser.getCurrentTokenId()) {
      case JsonTokenId.ID_NUMBER_FLOAT: {
        BigDecimal value = parser.getDecimalValue();
        long seconds = value.longValue();
        int nanoseconds = DecimalUtils.extractNanosecondDecimal(value, seconds);
        return fromNanoseconds.apply(new FromDecimalArguments(
                seconds, nanoseconds, getZone(context)));
      }

      case JsonTokenId.ID_NUMBER_INT: {
        long timestamp = parser.getLongValue();
        if (context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
          return this.fromNanoseconds.apply(new FromDecimalArguments(
                  timestamp, 0, this.getZone(context)
          ));
        }
        return this.fromMilliseconds.apply(new FromIntegerArguments(
                timestamp, this.getZone(context)
        ));
      }

      case JsonTokenId.ID_STRING: {
        String string = parser.getText().trim();
        if (string.length() == 0) {
          return null;
        }
        if (string.endsWith("+0000")) {
          string = string.substring(0, string.length() - 5) + "Z";
        }
        T value;
        try {
          TemporalAccessor acc = _formatter.parse(string);
          value = parsedToValue.apply(acc);
          if (context.isEnabled(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)) {
            return adjust.apply(value, this.getZone(context));
          }
        } catch (DateTimeException e) {
          throw _peelDTE(e);
        }
        return value;
      }
    }
    throw context.mappingException("Expected type float, integer, or string.");
  }

  private ZoneId getZone(DeserializationContext context) {
    // Instants are always in UTC, so don't waste compute cycles
    return (_valueClass == Instant.class) ? null : DateTimeUtils.timeZoneToZoneId(context.getTimeZone());
  }

  private static class FromIntegerArguments {
    public final long value;
    public final ZoneId zoneId;

    private FromIntegerArguments(long value, ZoneId zoneId) {
      this.value = value;
      this.zoneId = zoneId;
    }
  }

  private static class FromDecimalArguments {
    public final long integer;
    public final int fraction;
    public final ZoneId zoneId;

    private FromDecimalArguments(long integer, int fraction, ZoneId zoneId) {
      this.integer = integer;
      this.fraction = fraction;
      this.zoneId = zoneId;
    }
  }
}
