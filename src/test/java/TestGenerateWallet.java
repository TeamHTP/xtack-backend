import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.WalletGenerationResult;
import io.xpring.xrpl.XpringKitException;
import org.junit.Test;

public class TestGenerateWallet {

    @Test
    public void testGenerateWallet() throws XpringKitException {
        WalletGenerationResult walletGenerationResult = Wallet.generateRandomWallet();
        System.out.println(walletGenerationResult.getMnemonic());
    }

}
