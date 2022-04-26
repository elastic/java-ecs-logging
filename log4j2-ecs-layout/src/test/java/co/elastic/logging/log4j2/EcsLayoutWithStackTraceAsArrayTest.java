package co.elastic.logging.log4j2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.logging.log4j.core.LoggerContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EcsLayoutWithStackTraceAsArrayTest extends Log4j2EcsLayoutTest {

    @Override
    protected EcsLayout.Builder configureLayout(LoggerContext context) {
        return super.configureLayout(context)
                .setExceptionPattern("%cEx")
                .setStackTraceAsArray(true);
    }

    @Test
    void testLogException() throws Exception {
        error("test", new RuntimeException("test"));
        JsonNode log = getLastLogLine();
        assertThat(log.get("log.level").textValue()).isIn("ERROR", "SEVERE");
        assertThat(log.get("error.message").textValue()).isEqualTo("test");
        assertThat(log.get("error.type").textValue()).isEqualTo(RuntimeException.class.getName());
        assertThat(log.get("error.stack_trace").isArray()).isTrue();
        ArrayNode arrayNode = (ArrayNode) log.get("error.stack_trace");
        assertThat(arrayNode.size()).isEqualTo(2);
        assertThat(arrayNode.get(0).textValue()).isEqualTo("java.lang.RuntimeException: test");
        assertThat(arrayNode.get(1).textValue()).isEqualTo("STACK_TRACE!");
    }

    @Test
    void testLogExceptionNullMessage() throws Exception {
        error("test", new RuntimeException());
        JsonNode log = getLastLogLine();;
        assertThat(log.get("error.type").textValue()).isEqualTo(RuntimeException.class.getName());
        assertThat(log.get("error.message")).isNull();
        assertThat(log.get("error.stack_trace").isArray()).isTrue();
        ArrayNode arrayNode = (ArrayNode) log.get("error.stack_trace");
        assertThat(arrayNode.size()).isEqualTo(2);
        assertThat(arrayNode.get(0).textValue()).isEqualTo("java.lang.RuntimeException");
        assertThat(arrayNode.get(1).textValue()).isEqualTo("STACK_TRACE!");
    }
}
