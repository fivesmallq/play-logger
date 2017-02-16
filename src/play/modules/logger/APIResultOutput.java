package play.modules.logger;

import com.alibaba.fastjson.JSON;
import play.mvc.results.Error;

/**
 * @author <a href="mailto:fivesmallq@gmail.com">fivesmallq</a>
 * @version Revision: 1.0
 * @date 2017/2/16 下午6:36
 */
public class APIResultOutput extends DefaultResultOutput implements ResultOutput {
    @Override
    public String toString(Error result) {
        String message = result.getMessage();
        try {
            APIError error = JSON.parseObject(message, APIError.class);
            return result.getClass().getSimpleName() + " " + error.getCode() + " \"" + error.getMessage() + "\"";
        } catch (Exception e) {
            return message;
        }
    }
}
