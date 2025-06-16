/*
 * The MIT License
 *
 * Copyright 2013 Sony Mobile Communications AB. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.sonymobile.tools.gerrit.gerritevents.watchdog;

import com.jcraft.jsch.JSchException;
import com.sonymobile.tools.gerrit.gerritevents.ConnectionListener;
import com.sonymobile.tools.gerrit.gerritevents.GerritConnection;
import com.sonymobile.tools.gerrit.gerritevents.GerritHandler;
import com.sonymobile.tools.gerrit.gerritevents.ssh.Authentication;
import com.sonymobile.tools.gerrit.gerritevents.mock.SshdServerMock;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.channel.ChannelSession;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

import static com.sonymobile.tools.gerrit.gerritevents.mock.SshdServerMock.GERRIT_STREAM_EVENTS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

//CS IGNORE MagicNumber FOR NEXT 200 LINES. REASON: TestData

/**
 * Tests for {@link StreamWatchdog}.
 *
 * @author Robert Sandell &lt;robert.sandell@sonymobile.com&gt;
 */
public class StreamWatchdogTest {

    private static int sshPort;

    /**
     * Setup before all tests.
     */
    @BeforeClass
    public static void setUp() {
        try {
            sshPort = new Integer(System.getProperty("gerrit.ssh.port"));
        } catch (Exception ex) {
            sshPort = SshdServerMock.GERRIT_SSH_PORT;
        }
    }

    /**
     * Tests that the {@link StreamWatchdog} actually performs a restart of the connection.
     *
     * @throws IOException if so.
     * @throws InterruptedException if so.
     * @throws NoSuchMethodException if so.
     * @throws JSchException if so.
     */
    @Test(timeout = 2 * 60 * 60 * 1000)
    public void testFullTimeoutFlow() throws IOException, InterruptedException, NoSuchMethodException, JSchException {
        System.out.println("====This will be a long running test ca. 2 minutes=====");
        SshdServerMock.KeyPairFiles sshKey = SshdServerMock.generateKeyPair();
        SshdServerMock server = new SshdServerMock();
        SshServer sshd = SshdServerMock.startServer(sshPort, server);
        server.returnCommandFor("gerrit version", SshdServerMock.EofCommandMock.class);
        server.returnCommandFor("gerrit ls-projects", SshdServerMock.EofCommandMock.class);
        server.returnCommandFor(GERRIT_STREAM_EVENTS, WaitLongTimeCommand.class, true,
                new Object[]{MINUTES.toMillis(5)}, new Class<?>[]{Long.class});
        server.returnCommandFor(GERRIT_STREAM_EVENTS, SshdServerMock.CommandMock.class);
        GerritConnection connection = new GerritConnection("", "localhost", sshPort, "", "",
                new Authentication(sshKey.getPrivateKey(), "jenkins"), 20,
                new WatchTimeExceptionData(new int[0], Collections.<WatchTimeExceptionData.TimeSpan>emptyList()));
        Listen connectionListener = new Listen();
        connection.addListener(connectionListener);
        GerritHandler handler = new GerritHandler();
        connection.setHandler(handler);
        Thread connectionThread = new Thread(connection);
        connectionThread.start();
        server.waitForCommand(GERRIT_STREAM_EVENTS, 8000);
        Thread.sleep(2000);
        assertTrue(connectionListener.isConnectionEstablished());
        //wait for the connection to go down.
        connectionListener.waitForConnectionDown();
        server.waitForCommand(GERRIT_STREAM_EVENTS, 8000);
        Thread.sleep(1000);
        assertTrue(connectionListener.isConnectionEstablished());
        assertEquals(1, connection.getReconnectCallCount());
        System.out.println("====Shutting down GerritConnection=====");
        connection.shutdown(true);
        System.out.println("====Shutting down GerritHandler=====");
        handler.shutdown(true);
        System.out.println("====Shutting down SSHD=====");
        sshd.stop(true);
        System.out.println("====Done=====");
    }


    /**
     * ConnectionListener to help with the testing to see that the connection actually goes down and up.
     */
    public static class Listen implements ConnectionListener {

        boolean connectionEstablished = false;
        boolean connectionDown = true;

        @Override
        public synchronized void connectionEstablished() {
            connectionEstablished = true;
            connectionDown = false;
            this.notifyAll();
        }

        @Override
        public synchronized void connectionDown() {
            connectionDown = true;
            connectionEstablished = false;
            this.notifyAll();
        }

        /**
         * If the connection is established. I.e. if {@link #connectionEstablished()} has just been called.
         *
         * @return true if so.
         */
        public synchronized boolean isConnectionEstablished() {
            return connectionEstablished;
        }

        /**
         * If the connection is down. I.e. if {@link #connectionDown()} has just been called.
         *
         * @return true if so.
         */
        public synchronized boolean isConnectionDown() {
            return connectionDown;
        }

        /**
         * Waits for the {@link #connectionDown()} signal and returns after that.
         *
         * @throws InterruptedException if so.
         */
        public void waitForConnectionDown() throws InterruptedException {
            System.out.println("Waiting for connection to go down...");
            if (isConnectionDown()) {
                System.out.println("Connection is down!");
                return;
            }
            while (!isConnectionDown()) {
                synchronized (this) {
                    this.wait(1000);
                }
            }
            System.out.println("Connection is down!");
        }
    }

    /**
     * A SSH command that sleeps for a specified period of time.
     */
    public static class WaitLongTimeCommand extends SshdServerMock.CommandMock implements Runnable {

        private long timeout;
        private Thread thread;

        /**
         * Standard constructor. Will sleep for two minutes.
         *
         * @param command the command to "execute".
         */
        public WaitLongTimeCommand(String command) {
            super(command);
            timeout = MINUTES.toMillis(2);
        }

        /**
         * Standard constructor.
         *
         * @param command the command to "execute".
         * @param timeout millis to sleep for.
         */
        public WaitLongTimeCommand(String command, Long timeout) {
            super(command);
            this.timeout = timeout;
        }

        @Override
        public void start(ChannelSession channel, Environment environment) throws IOException {
            thread = new Thread(this, "WaitLongTimeCommand " + this.command);
            thread.setDaemon(true);
            thread.start();
        }

        @Override
        public void run() {
            System.out.println("WaitLongTimeCommand starting...");
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                System.err.println("WaitLongTimeCommand interrupted!");
            }
            System.out.println("WaitLongTimeCommand finished.");
            stop(0);
        }
    }
}
