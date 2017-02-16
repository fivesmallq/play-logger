package play.modules.logger;

import org.apache.commons.lang.StringUtils;
import play.mvc.results.*;
import play.mvc.results.Error;

import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * default impl for result output.
 *
 * @author <a href="mailto:fivesmallq@gmail.com">fivesmallq</a>
 * @version Revision: 1.0
 * @date 2017/2/16 下午5:58
 */
public class DefaultResultOutput implements ResultOutput {

    @Override
    public String toString(Redirect result) {
        return result.getClass().getSimpleName() + ' ' + result.url;
    }

    @Override
    public String toString(RenderTemplate result) {
        return "RenderTemplate " + result.getName() + " " + result.getRenderTime() + " ms";
    }

    @Override
    public String toString(RenderBinary result) {
        return Stream.of(result.getClass().getSimpleName(), result.getName(), result.getContentType())
                .filter(StringUtils::isNotEmpty)
                .collect(joining(" "));
    }

    @Override
    public String toString(Error result) {
        return result.getClass().getSimpleName() + " \"" + result.getMessage() + "\"";
    }
}
