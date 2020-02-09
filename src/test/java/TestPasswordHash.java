import org.junit.Test;
import tech.xtack.api.auth.AuthUtils;

import java.security.NoSuchAlgorithmException;

public class TestPasswordHash {

    @Test
    public void testHashPassword() throws NoSuchAlgorithmException {
        System.out.println(AuthUtils.hashPassword("asdf"));
    }

}
