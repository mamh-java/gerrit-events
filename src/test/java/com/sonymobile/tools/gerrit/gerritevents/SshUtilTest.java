package com.sonymobile.tools.gerrit.gerritevents;

import java.io.File;
import java.net.URL;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.sonymobile.tools.gerrit.gerritevents.ssh.SshUtil;

/**
 * Test that SSH keys are correctly handled for keys with and without passphrases
 * Created by svanoort on 9/11/15.
 */
public class SshUtilTest {

    static final String PASSPHRASE = "letmein";

    /**
     * Test basic key file loading.
     * @throws Exception If test fails.
     */
    @Test
    public void testBasicKeyFileLoadingRSA() throws Exception {

        boolean tested = SshUtil.isPrivateKeyFileValid(null);
        assertFalse("Key file validation failed with null key file", tested);

        tested = SshUtil.isPrivateKeyFileValid(new File("missing_file"));
        assertFalse("Key file validation failed with missing key file", tested);

        URL url = Thread.currentThread().getContextClassLoader().getResource(
                "com/sonymobile/tools/gerrit/gerritevents/id_rsa_corrupted");
        tested = SshUtil.isPrivateKeyFileValid(new File(url.getPath()));
        assertFalse("Key file validation failed with invalid key file", tested);

        url = Thread.currentThread().getContextClassLoader().getResource(
                "com/sonymobile/tools/gerrit/gerritevents/id_rsa");
        tested = SshUtil.isPrivateKeyFileValid(new File(url.getPath()));
        assertTrue("Key file validation pass with vallid key file", tested);
    }

    /**
     * Test basic key file loading.
     * @throws Exception If test fails.
     */
    @Test
    public void testBasicKeyFileLoadingED25519() throws Exception {

        boolean tested = SshUtil.isPrivateKeyFileValid(null);
        assertFalse("Key file validation failed with null key file", tested);

        tested = SshUtil.isPrivateKeyFileValid(new File("missing_file"));
        assertFalse("Key file validation failed with missing key file", tested);

        URL url = Thread.currentThread().getContextClassLoader().getResource(
                "com/sonymobile/tools/gerrit/gerritevents/id_ed25519_corrupted");
        tested = SshUtil.isPrivateKeyFileValid(new File(url.getPath()));
        assertFalse("Key file validation failed with invalid key file", tested);

        url = Thread.currentThread().getContextClassLoader().getResource(
                "com/sonymobile/tools/gerrit/gerritevents/id_ed25519");
        tested = SshUtil.isPrivateKeyFileValid(new File(url.getPath()));
        assertTrue("Key file validation pass with vallid key file", tested);
    }

    /**
     * Test handling of keys with no passphrase set.
     * @throws Exception If test fails.
     */
    @Test
    public void testInvalidKeyFile() throws Exception {
        File file = new File("missing_file");

        boolean tested = SshUtil.checkPassPhrase(file, null);
        assertFalse("Passphrase validation failed with missing key file and null passphrase", tested);

        tested = SshUtil.checkPassPhrase(file, "");
        assertFalse("Passphrase validation failed with missing key file and empty passphrase", tested);

        tested = SshUtil.checkPassPhrase(file, "nope");
        assertFalse("Passphrase validation failed with missing key file and one passphrase", tested);
    }

    /**
     * Test handling of keys with no passphrase set.
     * @throws Exception If test fails.
     */
    @Test
    public void testNoPassphraseParsingRSA() throws Exception {
        // Get no-passphrase key resource as file
        URL url = Thread.currentThread().getContextClassLoader().getResource(
                "com/sonymobile/tools/gerrit/gerritevents/id_rsa");
        File file = new File(url.getPath());

        boolean tested = SshUtil.checkPassPhrase(file, null);
        assertTrue("Passphrase validation failed to validate null passphrase with none set", tested);

        tested = SshUtil.checkPassPhrase(file, "");
        assertTrue("Passphrase validation failed to validate empty passphrase with none set", tested);

        tested = SshUtil.checkPassPhrase(file, "nope");
        assertFalse("Passphrase validation passed incorrectly on a file with none set", tested);
    }

    /**
     * Test handling of keys with no passphrase set.
     * @throws Exception If test fails.
     */
    @Test
    public void testNoPassphraseParsingED25519() throws Exception {
        // Get no-passphrase key resource as file
        URL url = Thread.currentThread().getContextClassLoader().getResource(
                "com/sonymobile/tools/gerrit/gerritevents/id_ed25519");
        File file = new File(url.getPath());

        boolean tested = SshUtil.checkPassPhrase(file, null);
        assertTrue("Passphrase validation failed to validate null passphrase with none set", tested);

        tested = SshUtil.checkPassPhrase(file, "");
        assertTrue("Passphrase validation failed to validate empty passphrase with none set", tested);

        tested = SshUtil.checkPassPhrase(file, "nope");
        assertFalse("Passphrase validation passed incorrectly on a file with none set", tested);
    }

    /**
     * Test that keys can be correctly decrypted with a passphrase set.
     * @throws Exception If test fails.
     */
    @Test
    public void testPassphraseParsingRSA() throws Exception {

        // Get passphrase-encrypted keyfile as file
        URL url = Thread.currentThread().getContextClassLoader().getResource(
                "com/sonymobile/tools/gerrit/gerritevents/id_rsa_passphrase");
        File file = new File(url.getPath());

        // Fail if invalid passphrase does not fail
        SshUtil.checkPassPhrase(file, "wrongpassphrase");
        boolean failure = SshUtil.checkPassPhrase(file, "wrongpassphrase");
        assertFalse("Passphrase validation should fail!", failure);

        failure = SshUtil.checkPassPhrase(file, null);
        assertFalse("Passphrase validation should fail if passphrase is set and none supplied", failure);

        failure = SshUtil.checkPassPhrase(file, "");
        assertFalse("Passphrase validation should fail if passphrase is set and none is set", failure);

        // This used to fail with an AES encrypted key
        boolean tested = SshUtil.checkPassPhrase(file, PASSPHRASE);
        assertTrue("Passphrase validation failed!", tested);
    }

    /**
     * Test that keys can be correctly decrypted with a passphrase set.
     * @throws Exception If test fails.
     */
    @Test
    public void testPassphraseParsingED25519() throws Exception {

        // Get passphrase-encrypted keyfile as file
        URL url = Thread.currentThread().getContextClassLoader().getResource(
                "com/sonymobile/tools/gerrit/gerritevents/id_ed25519_passphrase");
        File file = new File(url.getPath());

        // Fail if invalid passphrase does not fail
        SshUtil.checkPassPhrase(file, "wrongpassphrase");
        boolean failure = SshUtil.checkPassPhrase(file, "wrongpassphrase");
        assertFalse("Passphrase validation should fail!", failure);

        failure = SshUtil.checkPassPhrase(file, null);
        assertFalse("Passphrase validation should fail if passphrase is set and none supplied", failure);

        failure = SshUtil.checkPassPhrase(file, "");
        assertFalse("Passphrase validation should fail if passphrase is set and none is set", failure);

        // This used to fail with an AES encrypted key
        boolean tested = SshUtil.checkPassPhrase(file, PASSPHRASE);
        assertTrue("Passphrase validation failed!", tested);
    }
}
