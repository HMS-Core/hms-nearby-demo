/*
Copyright 2018 Tianyi Zhang

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


    2020.3.15 - Replaced Kryonet by HMS Nearby Service.
    2020.3.15 - Add Chinese version.
    2020.3.15 - Add Game speed selection.
                Huawei Technologies Co.,Ltd. <wangmingqi@huawei.com>
 */

package com.huawei.nearby.game.snake.agents;

import android.os.RemoteException;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.huawei.hms.nearby.StatusCode;
import com.huawei.hms.nearby.discovery.ConnectCallback;
import com.huawei.hms.nearby.discovery.ConnectInfo;
import com.huawei.hms.nearby.discovery.ConnectResult;
import com.huawei.hms.nearby.discovery.DiscoveryEngine;
import com.huawei.hms.nearby.discovery.Policy;
import com.huawei.hms.nearby.discovery.ScanEndpointCallback;
import com.huawei.hms.nearby.discovery.ScanEndpointInfo;
import com.huawei.hms.nearby.discovery.ScanOption;
import com.huawei.hms.nearby.transfer.Data;
import com.huawei.hms.nearby.transfer.DataCallback;
import com.huawei.hms.nearby.transfer.TransferEngine;
import com.huawei.hms.nearby.transfer.TransferStateUpdate;
import com.huawei.nearby.game.snake.App;
import com.huawei.nearby.game.snake.elements.ClientSnapshot;
import com.huawei.nearby.game.snake.helpers.Constants;
import com.huawei.nearby.game.snake.helpers.Utils;
import com.huawei.nearby.game.snake.states.GameState;
import com.huawei.nearby.game.snake.states.client.LookForServerState;
import com.huawei.nearby.game.snake.states.client.MainGameState;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class Client extends IAgent {
    private static final String TAG = "NearbyGameSnake";

    private TransferEngine mTransferEngine;

    private DiscoveryEngine mDiscoveryEngine;

    private final int randomEndpointName = (int) System.currentTimeMillis() % 1000 + 1000;

    private String myNameStr = "" + randomEndpointName;

    private String myServiceId = "NearbySnakeServiceId";

    private String mRemoteEndpointId;

    private App _app;

    private Map<String, Long> lagMap = new HashMap<String, Long>();

    public Client(TransferEngine mTransferEngine, DiscoveryEngine mDiscoveryEngine, App app) {
        this.mTransferEngine = mTransferEngine;
        this.mDiscoveryEngine = mDiscoveryEngine;
        this._app = app;
    }

    @Override
    public void broadcast() throws IOException {

    }

    @Override
    public void lookForServer(final Runnable errorCallback) {
        try {
            doStartScan();
        } catch (RemoteException e) {
            Gdx.app.error(TAG, e.getMessage());
            Gdx.app.postRunnable(errorCallback);
        }
    }

    @Override
    public void send(byte[] packet) {
        Data data = Data.fromBytes(packet);
        Log.d(TAG, "Client sent data.to id:" + mRemoteEndpointId + " Data id: " + data.getId());
        mTransferEngine.sendData(mRemoteEndpointId, data);
    }

    @Override
    public void destroy() {
        mDiscoveryEngine.stopScan();
        mDiscoveryEngine.disconnectAll();
    }

    @Override
    public void setLagDetectionStartTime(String seq, long ms) {
        lagMap.put(seq, ms);
    }

    public void doStartScan() throws RemoteException {
        ScanOption.Builder discBuilder = new ScanOption.Builder();
        discBuilder.setPolicy(Policy.POLICY_STAR);
        Log.d(TAG, "Start Scan.");
        mDiscoveryEngine.startScan(myServiceId, mDiscCb, discBuilder.build());
    }

    private void setGameSpeed(ConnectInfo connectionInfo) {
        if (connectionInfo.getEndpointName().equals("Speed500")) {
            Constants.MOVE_EVERY_MS = 500;
        } else if (connectionInfo.getEndpointName().equals("Speed333")) {
            Constants.MOVE_EVERY_MS = 333;
        } else if (connectionInfo.getEndpointName().equals("Speed250")) {
            Constants.MOVE_EVERY_MS = 250;
        }

        return;
    }

    // when client connected server. Change UI
    private void serverConnected() {
        GameState state = _app.getCurState();
        if (state instanceof LookForServerState) {
            LookForServerState lookforServerState = (LookForServerState) state;
            lookforServerState.lblInfo.setText("OK!");
            byte[] bytes = "Hello".getBytes(Charset.defaultCharset());
            send(bytes);
        }
    }

    private void disconnectFromServer() {
        GameState state = _app.getCurState();
        if (state instanceof LookForServerState) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    _app.gotoErrorScreen("No! We lost contact with the host!");
                }
            });
        } else if (state instanceof MainGameState) {
            MainGameState mainGameState = (MainGameState) state;
            if (mainGameState.gameResult.get() != null) {
                return;
            }
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    _app.gotoErrorScreen("No! We lost contact with the host!");
                }
            });

        }
    }

    private ConnectCallback mConnCb = new ConnectCallback() {
        @Override
        public void onEstablish(String endpointId, ConnectInfo connectionInfo) {
            mDiscoveryEngine.acceptConnect(endpointId, mDataCb);
            Log.d(TAG, "Nearby Client accept connection from:" + endpointId);
            // Server put game speed config in its endpoint name.
            setGameSpeed(connectionInfo);
        }

        @Override
        public void onResult(String endpointId, ConnectResult result) {
            switch (result.getStatus().getStatusCode()) {
                case StatusCode.STATUS_SUCCESS:
                    Log.d(TAG, "Nearby connection was established successfully. Remote ID:" + endpointId);
                    mDiscoveryEngine.stopScan();
                    mRemoteEndpointId = endpointId;
                    /* The connection was established successfully, we can exchange data. */
                    serverConnected();
                    break;
                case StatusCode.STATUS_CONNECT_REJECTED:
                    /* The Connection was rejected. */
                    Log.d(TAG, "Nearby connection was rejected. Remote ID:" + endpointId);
                    break;
                default:
                    /* other unknown status code. */
                    Log.d(TAG, "Nearby connection catch status code. Remote ID:" + endpointId + ", Status Code:"
                        + result.getStatus().getStatusCode());
            }
        }

        @Override
        public void onDisconnected(String endpointId) {
            Log.d(TAG, "Nearby Client Disconnected from:" + endpointId);
            disconnectFromServer();
        }
    };

    private ScanEndpointCallback mDiscCb = new ScanEndpointCallback() {
        @Override
        public void onFound(String endpointId, ScanEndpointInfo discoveryEndpointInfo) {
            mRemoteEndpointId = endpointId;
            mDiscoveryEngine.requestConnect(myNameStr, mRemoteEndpointId, mConnCb);
            Log.d(TAG, "Nearby Client found Server and request connection. Server id:" + endpointId);
        }

        @Override
        public void onLost(String endpointId) {
            Log.d(TAG, "Nearby Lost endpoint: " + endpointId);
        }
    };

    private void processServerData(Data data) {
        GameState state = _app.getCurState();

        /* receive first server game data will trigger LookForServerState -> MainGameState */
        if (state instanceof LookForServerState) {
            Log.d(TAG, "Client received data. Current State Look for server");
            LookForServerState lookforServerState = (LookForServerState) state;

            // should not receive lag detection here unless packet disorder
            String str = new String(data.asBytes());
            if (str.contains("ServerLagDetection")) {
                return;
            }

            if (lookforServerState.gameStarted.get()) {
                return;
            }
            lookforServerState.gameStarted.set(true);
            Log.d(TAG, "game Started");
            // The MainGameState instance must be created by Gdx, because it initializes an OpenGL renderer,
            // which must be created on the thread that has an OpenGL context.
            final int id = randomEndpointName;
            final long receivedNanoTime = Utils.getNanoTime();
            final byte[] update = data.asBytes();
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    _app.setState(new MainGameState(_app, id, receivedNanoTime, update));
                }
            });

        } else if (state instanceof MainGameState) {
            String str = new String(data.asBytes());
            MainGameState mainGameState = (MainGameState) state;
            /* echo server LAG detect packet back */
            if (str.contains("ServerLagDetection")) {
                send(data.asBytes());
            } else if (str.contains("ClientLagDetection")) {
                /* receive echoed client LAG detect packet. calculate LAG. */
                long starttime = lagMap.get(str);
                mainGameState.lag = "" + (System.currentTimeMillis() - starttime) + " ms";
                Log.d(TAG, "LAG: " + mainGameState.lag);
            } else {
                /* process server game data */
                byte[] bytes = data.asBytes();
                ClientSnapshot clientSnapshot = mainGameState.getClientSnapshot();
                clientSnapshot.onServerUpdate(parseServerUpdate(bytes));
            }
        }
    }

    private DataCallback mDataCb = new DataCallback() {
        @Override
        public void onReceived(String endpointId, Data data) {
            Log.d(TAG, "Client received data. from : " + endpointId + " Data id:" + data.getId());
            processServerData(data);
        }

        @Override
        public void onTransferUpdate(String string, TransferStateUpdate update) {
        }
    };
}
