package io.jenkins.plugins.grading;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import hudson.tasks.junit.TestResultAction;

import static io.jenkins.plugins.grading.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the class {@link TestScore}.
 *
 * @author Eva-Maria Zeintl
 * @author Ullrich Hafner
 * @author Lukas Kirner
 */

@RunWith(Parameterized.class)
public class TestScoreTest {

    @Parameter
    public TestConfiguration configuration;

    @Parameter(1)
    public TestResultAction resultAction;

    @Parameter(2)
    public int expectedTotalImpact;

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {
                createTestConfiguration(25,-1, -2, 1),
                createAction(8, 1, 1),
                3
            },
            {
                createTestConfiguration(25,-1, -2, 1),
                createAction(8, 5, 1),
                -9
            },
            {
                createTestConfiguration(25,-1, -2, -1),
                createAction(8, 5, 1),
                -13
            },
            {
                createTestConfiguration(25,0, 0, 0),
                createAction(0, 0, 0),
                0
            },
            {
                createTestConfiguration(25,99, 99, 99),
                createAction(0, 0, 0),
                0
            },
            {
                createTestConfiguration(25,1, 1, 1),
                createAction(3, 3, 0),
                3
            },
        });
    }

    @Test
    public void test() {
        TestScore test = new TestScore(configuration, resultAction);
        assertThat(test.getTotalSize()).isEqualTo(resultAction.getTotalCount());
        assertThat(test.getPassedSize()).isEqualTo(resultAction.getTotalCount() - resultAction.getFailCount() - resultAction.getSkipCount());
        assertThat(test.getFailedSize()).isEqualTo(resultAction.getFailCount());
        assertThat(test.getSkippedSize()).isEqualTo(resultAction.getSkipCount());
        assertThat(test.getId()).isEqualTo(resultAction.getDisplayName());
        assertThat(test.getTotalImpact()).isEqualTo(expectedTotalImpact);
    }

    private static TestConfiguration createTestConfiguration(
            final int maxScore, final int skippedImpact, final int failureImpact, final int passedImpact) {
        return new TestConfiguration.TestConfigurationBuilder()
            .setMaxScore(maxScore)
            .setSkippedImpact(skippedImpact)
            .setFailureImpact(failureImpact)
            .setPassedImpact(passedImpact)
            .build();
    }

    private static TestResultAction createAction(final int totalSize, final int failedSize, final int skippedSize) {
        TestResultAction action = mock(TestResultAction.class);
        when(action.getTotalCount()).thenReturn(totalSize);
        when(action.getFailCount()).thenReturn(failedSize);
        when(action.getSkipCount()).thenReturn(skippedSize);
        return action;
    }
}
