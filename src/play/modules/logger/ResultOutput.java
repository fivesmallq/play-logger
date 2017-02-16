package play.modules.logger;

import play.mvc.results.Error;
import play.mvc.results.*;

/**
 * result output
 * @author <a href="mailto:fivesmallq@gmail.com">fivesmallq</a>
 * @version Revision: 1.0
 * @date 2017/2/16 下午5:56
 */
public interface ResultOutput {
    String toString(Redirect result);

    String toString(RenderTemplate result);

    String toString(RenderBinary result);

    String toString(Error result);
}
