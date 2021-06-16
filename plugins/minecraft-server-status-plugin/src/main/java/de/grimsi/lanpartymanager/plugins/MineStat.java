/*
 * MineStat.java - A Minecraft server status checker
 * Copyright (C) 2014 Lloyd Dilley
 * http://www.dilley.me/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

/**
 * @authors Lloyd Dilley, grimsi
 */

package de.grimsi.gameradar.plugins;

import lombok.Data;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

@Data
public class MineStat {
    private static final byte NUM_FIELDS = 6;      // number of values expected from server
    private static final byte NUM_FIELDS_BETA = 3; // number of values expected from a 1.8b/1.3 server
    private static final int DEFAULT_TIMEOUT = 5;  // default TCP timeout in seconds

    public enum Retval {
        SUCCESS(0), CONNFAIL(-1), TIMEOUT(-2), UNKNOWN(-3);

        private final int retval;

        private Retval(int retval) {
            this.retval = retval;
        }

        public int getRetval() {
            return retval;
        }
    }

    /**
     * Hostname or IP address of the Minecraft server
     */
    private String address;

    /**
     * Port number the Minecraft server accepts connections on
     */
    private int port;

    /**
     * TCP socket connection timeout in seconds
     */
    private int timeout;

    /**
     * Is the server up? (true or false)
     */
    private boolean serverUp;

    /**
     * Message of the day from the server
     */
    private String motd;

    /**
     * Minecraft version the server is running
     */
    private String version;

    /**
     * Current number of players on the server
     */
    private String currentPlayers;

    /**
     * Maximum player capacity of the server
     */
    private String maximumPlayers;

    /**
     * Ping time to server in milliseconds
     */
    private long latency;

    MineStat(String address, int port) {
        this(address, port, DEFAULT_TIMEOUT);
    }

    MineStat(String address, int port, int timeout) {
        setAddress(address);
        setPort(port);
        setTimeout(timeout);
        /*
         * Try the newest protocol first and work down. If the query succeeds or the
         * connection fails, there is no reason to continue with subsequent queries.
         * Attempts should continue in the event of a timeout however since it may
         * be due to an issue during the handshake.
         * Note: Newer server versions may still respond to older ping query types.
         * For example, 1.13.2 responds to 1.4/1.5 queries, but not 1.6 queries.
         */
        // 1.7
        Retval retval = jsonQuery(address, port, getTimeout());
        // 1.6
        if (retval != Retval.SUCCESS && retval != Retval.CONNFAIL) {
            retval = newQuery(address, port, getTimeout());
        }
        // 1.4/1.5
        if (retval != Retval.SUCCESS && retval != Retval.CONNFAIL) {
            retval = legacyQuery(address, port, getTimeout());
        }
        // 1.8b/1.3
        if (retval != Retval.SUCCESS && retval != Retval.CONNFAIL) {
            retval = betaQuery(address, port, getTimeout());
        }
    }

    private int getTimeout() {
        return timeout * 1000;
    } // convert to milliseconds

    /*
     * 1.8b/1.3
     * 1.8 beta through 1.3 servers communicate as follows for a ping query:
     * 1. Client sends \xFE (server list ping)
     * 2. Server responds with:
     *   2a. \xFF (kick packet)
     *   2b. data length
     *   2c. 3 fields delimited by \u00A7 (section symbol)
     * The 3 fields, in order, are: message of the day, current players, and max players
     */
    private Retval betaQuery(String address, int port, int timeout) {
        try {
            String[] serverData = null;
            byte[] rawServerData = null;
            Socket clientSocket = new Socket();
            long startTime = System.currentTimeMillis();
            clientSocket.connect(new InetSocketAddress(getAddress(), getPort()), getTimeout());
            setLatency(System.currentTimeMillis() - startTime);
            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
            DataInputStream dis = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            dos.writeBytes("\u00FE");
            if (dis.readUnsignedByte() == 0xFF) // kick packet (255)
            {
                int dataLen = dis.readUnsignedShort();
                rawServerData = new byte[dataLen * 2];
                dis.read(rawServerData, 0, dataLen * 2);
                clientSocket.close();
            } else {
                clientSocket.close();
                return Retval.UNKNOWN;
            }

            serverData = new String(rawServerData, StandardCharsets.UTF_16).split("\u00A7"); // section symbol
            if (serverData.length >= NUM_FIELDS_BETA) {
                setVersion("1.8b/1.3"); // since server does not return version, set it
                setMotd(serverData[0]);
                setCurrentPlayers(serverData[1]);
                setMaximumPlayers(serverData[2]);
                serverUp = true;
            } else {
                return Retval.UNKNOWN;
            }
        } catch (SocketTimeoutException ste) {
            return Retval.TIMEOUT;
        } catch (IOException ce) {
            return Retval.CONNFAIL;
        } catch (Exception e) {
            serverUp = false;
            return Retval.UNKNOWN;
        }
        return Retval.SUCCESS;
    }

    /*
     * 1.4/1.5
     * 1.4 and 1.5 servers communicate as follows for a ping query:
     * 1. Client sends:
     *   1a. \xFE (server list ping)
     *   1b. \x01 (server list ping payload)
     * 2. Server responds with:
     *   2a. \xFF (kick packet)
     *   2b. data length
     *   2c. 6 fields delimited by \x00 (null)
     * The 6 fields, in order, are: the section symbol and 1, protocol version,
     * server version, message of the day, current players, and max players.
     * The protocol version corresponds with the server version and can be the
     * same for different server versions.
     */
    private Retval legacyQuery(String address, int port, int timeout) {
        try {
            String[] serverData = null;
            byte[] rawServerData = null;
            Socket clientSocket = new Socket();
            long startTime = System.currentTimeMillis();
            clientSocket.connect(new InetSocketAddress(getAddress(), getPort()), getTimeout());
            setLatency(System.currentTimeMillis() - startTime);
            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
            DataInputStream dis = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            dos.writeBytes("\u00FE\u0001");
            if (dis.readUnsignedByte() == 0xFF) // kick packet (255)
            {
                int dataLen = dis.readUnsignedShort();
                rawServerData = new byte[dataLen * 2];
                dis.read(rawServerData, 0, dataLen * 2);
                clientSocket.close();
            } else {
                clientSocket.close();
                return Retval.UNKNOWN;
            }

            serverData = new String(rawServerData, StandardCharsets.UTF_16).split("\u0000"); // null
            if (serverData.length >= NUM_FIELDS) {
                // serverData[0] contains the section symbol and 1
                // serverData[1] contains the protocol version (51 for example)
                setVersion(serverData[2]);
                setMotd(serverData[3]);
                setCurrentPlayers(serverData[4]);
                setMaximumPlayers(serverData[5]);
                serverUp = true;
            } else {
                return Retval.UNKNOWN;
            }
        } catch (SocketTimeoutException ste) {
            return Retval.TIMEOUT;
        } catch (IOException ce) {
            return Retval.CONNFAIL;
        } catch (Exception e) {
            serverUp = false;
            return Retval.UNKNOWN;
        }
        return Retval.SUCCESS;
    }

    /*
     * 1.6
     * 1.6 servers communicate as follows for a ping query:
     * 1. Client sends:
     *   1a. \xFE (server list ping)
     *   1b. \x01 (server list ping payload)
     *   1c. \xFA (plugin message)
     *   1d. \x00\x0B (11 which is the length of "MC|PingHost")
     *   1e. "MC|PingHost" encoded as a UTF-16BE string
     *   1f. length of remaining data as a short: remote address (encoded as UTF-16BE) + 7
     *   1g. arbitrary 1.6 protocol version (\x4E for example for 78)
     *   1h. length of remote address as a short
     *   1i. remote address encoded as a UTF-16BE string
     *   1j. remote port as an int
     * 2. Server responds with:
     *   2a. \xFF (kick packet)
     *   2b. data length
     *   2c. 6 fields delimited by \x00 (null)
     * The 6 fields, in order, are: the section symbol and 1, protocol version,
     * server version, message of the day, current players, and max players.
     * The protocol version corresponds with the server version and can be the
     * same for different server versions.
     */
    private Retval newQuery(String address, int port, int timeout) {
        try {
            String[] serverData = null;
            byte[] rawServerData = null;
            Socket clientSocket = new Socket();
            long startTime = System.currentTimeMillis();
            clientSocket.connect(new InetSocketAddress(getAddress(), getPort()), getTimeout());
            setLatency(System.currentTimeMillis() - startTime);
            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
            DataInputStream dis = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            dos.writeBytes("\u00FE\u0001\u00FA");
            dos.writeBytes("\u0000\u000B");    // 11 (length of "MC|PingHost")
            byte[] payload = "MC|PingHost".getBytes(StandardCharsets.UTF_16BE);
            dos.write(payload, 0, payload.length);
            dos.writeShort(7 + 2 * address.length());
            dos.writeBytes("\u004E");          // 78 (protocol version of 1.6.4)
            dos.writeShort(address.length());
            payload = address.getBytes(StandardCharsets.UTF_16BE);
            dos.write(payload, 0, payload.length);
            dos.writeInt(port);
            if (dis.readUnsignedByte() == 0xFF) // kick packet (255)
            {
                int dataLen = dis.readUnsignedShort();
                rawServerData = new byte[dataLen * 2];
                dis.read(rawServerData, 0, dataLen * 2);
                clientSocket.close();
            } else {
                clientSocket.close();
                return Retval.UNKNOWN;
            }

            serverData = new String(rawServerData, StandardCharsets.UTF_16).split("\u0000"); // null
            if (serverData.length >= NUM_FIELDS) {
                // serverData[0] contains the section symbol and 1
                // serverData[1] contains the protocol version (always 127 for >=1.7.x)
                setVersion(serverData[2]);
                setMotd(serverData[3]);
                setCurrentPlayers(serverData[4]);
                setMaximumPlayers(serverData[5]);
                serverUp = true;
            } else {
                return Retval.UNKNOWN;
            }
        } catch (SocketTimeoutException ste) {
            return Retval.TIMEOUT;
        } catch (IOException ce) {
            return Retval.CONNFAIL;
        } catch (Exception e) {
            serverUp = false;
            return Retval.UNKNOWN;
        }
        return Retval.SUCCESS;
    }

    /*
     * 1.7
     * 1.7 to current servers communicate as follows for a ping query:
     * 1. Client sends:
     *   1a. \x00 (handshake packet containing the fields specified below)
     *   1b. \x00 (request)
     * The handshake packet contains the following fields respectively:
     *   1. protocol version as a varint (\x00 suffices)
     *   2. remote address as a string
     *   3. remote port as an unsigned short
     *   4. state as a varint (should be 1 for status)
     * 2. Server responds with:
     *   2a. \x00 (JSON response)
     * An example JSON string contains:
     *   {'players': {'max': 20, 'online': 0},
     *   'version': {'protocol': 404, 'name': '1.13.2'},
     *   'description': {'text': 'A Minecraft Server'}}
     */
    private Retval jsonQuery(String address, int port, int timeout) {
        return Retval.UNKNOWN; // ToDo: Implement me!
    }
}