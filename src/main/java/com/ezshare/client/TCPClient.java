package com.ezshare.client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by mvalentino on 20/3/17.
 */
public class TCPClient {

    public void Execute() throws IOException {

        // TODO: pass this parameter from command line
        String hostName = "localhost";
        int portNumber = 44457;

        try (
                Socket echoSocket = new Socket(hostName, portNumber);
                PrintWriter out =
                        new PrintWriter(echoSocket.getOutputStream(), true);
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(echoSocket.getInputStream()));
                BufferedReader stdIn =
                        new BufferedReader(
                                new InputStreamReader(System.in))
        ) {
            String userInput;
            System.out.print("Your message: ");
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                System.out.println("echo: " + in.readLine());
                System.out.print("Your message: ");
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }

    }
}
