package com.github.estuaryoss.agent.unit;

import com.github.estuaryoss.agent.component.AppEnvironment;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AppEnvironmentTest {

    @Test
    public void whenSettingEnvVarsAndTheEnvIsCleanThenAllEnvVarsAreSet() {
        Map<String, String> envVarsToBeSet = new LinkedHashMap<>();
        envVarsToBeSet.put("FOO1", "BAR1");
        envVarsToBeSet.put("FOO2", "BAR2");

        Map<String, String> environment = new AppEnvironment().setVirtualEnvVars(envVarsToBeSet);

        assertThat(environment).isEqualTo(envVarsToBeSet);
    }

    @Test
    public void whenSettingEnvVarsAndDeletingAllEnvVarsThenVirtualEnvIsEmpty() {
        Map<String, String> envVarsToBeSet = new LinkedHashMap<>();
        envVarsToBeSet.put("FOO1", "BAR1");
        envVarsToBeSet.put("FOO2", "BAR2");
        AppEnvironment appEnvironment = new AppEnvironment();
        Map<String, String> envVarsSet = appEnvironment.setVirtualEnvVars(envVarsToBeSet);
        assertThat(envVarsSet).isEqualTo(envVarsToBeSet);

        appEnvironment.cleanVirtualEnv();
        assertThat(appEnvironment.getVirtualEnv().size()).isEqualTo(0);
    }

    @Test
    public void whenSettingAlreadyExistingSystemEnvVarThenIsNotSet() {
        Map<String, String> envVarsToBeSet = new LinkedHashMap<>();
        envVarsToBeSet.put("FOO1", "BAR1");
        envVarsToBeSet.put("JAVA_HOME", "BAR2"); // <- system one

        Map<String, String> environment = new AppEnvironment().setVirtualEnvVars(envVarsToBeSet);

        assertThat(environment).isNotEqualTo(envVarsToBeSet);
    }

    @Test
    public void whenSettingTwiceSameVirtualEnvVarThenIsSetEveryTime() {
        Map<String, String> envVarsToBeSet = new LinkedHashMap<>();
        envVarsToBeSet.put("FOO1", "BAR1");

        AppEnvironment environment = new AppEnvironment();
        Map<String, String> take1 = environment.setVirtualEnvVars(envVarsToBeSet);
        Map<String, String> take2 = environment.setVirtualEnvVars(envVarsToBeSet);

        assertThat(take1).isEqualTo(envVarsToBeSet);
        assertThat(take2).isEqualTo(envVarsToBeSet);
    }


    @Test
    public void whenSettingEmptyMapVirtualEnvVarThenResponseIsEmpty() {
        Map<String, String> envVarsToBeSet = new LinkedHashMap<>();

        AppEnvironment environment = new AppEnvironment();
        Map<String, String> envVarsAdded = environment.setVirtualEnvVars(envVarsToBeSet);

        assertThat(envVarsAdded).isEqualTo(envVarsToBeSet);
    }

    @Test
    public void whenSettingEnvVarWhichHasReferenceToAnotherEnvVar_TheSecondOneIsInterpolated() {
        String envVarName1 = "FOO1";
        String envVarName1Value = "/FOO1/BAR1";
        String envVarName2 = "FOO2";
        String envVarName2Value = "{FOO1}/BAR2";
        Map<String, String> envVarsToBeSet = new LinkedHashMap<>();
        envVarsToBeSet.put(envVarName1, envVarName1Value);
        envVarsToBeSet.put(envVarName2, envVarName2Value);

        AppEnvironment environment = new AppEnvironment();
        Map<String, String> envVarsAdded = environment.setVirtualEnvVars(envVarsToBeSet);

        assertThat(envVarsAdded.get(envVarName1)).isEqualTo(envVarName1Value);
        assertThat(environment.getVirtualEnv().get(envVarName1)).isEqualTo(envVarName1Value);
        assertThat(envVarsAdded.get(envVarName2)).isEqualTo("/FOO1/BAR1/BAR2");
        assertThat(environment.getVirtualEnv().get(envVarName2)).isEqualTo("/FOO1/BAR1/BAR2");
    }


    @Test
    public void whenSettingVirtualEnvVarsThenAHardLimitIsReached() {
        final int VIRTUAL_ENV_VARS_LIMIT_SIZE = AppEnvironment.VIRTUAL_ENVIRONMENT_MAX_SIZE;
        Map<String, String> envVarsToBeSet = new LinkedHashMap<>();

        for (int i = 0; i < 2 * VIRTUAL_ENV_VARS_LIMIT_SIZE; i++) {
            envVarsToBeSet.put(String.valueOf(i), String.valueOf(i));
        }

        AppEnvironment environment = new AppEnvironment();
        Map<String, String> envVarsAdded = environment.setVirtualEnvVars(envVarsToBeSet);

        assertThat(envVarsAdded).isNotEqualTo(envVarsToBeSet);
        assertThat(envVarsAdded.get(String.valueOf(VIRTUAL_ENV_VARS_LIMIT_SIZE))).isEqualTo(null);
    }

}
