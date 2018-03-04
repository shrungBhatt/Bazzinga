package com.projects.shrungbhatt.filetransfer.transfer;

import com.projects.shrungbhatt.filetransfer.model.DeviceDTO;


public class TransferModelGenerator {

    public static ITransferable generateDeviceTransferModelRequest(DeviceDTO device) {
        TransferModel transferModel = new TransferModel(TransferConstants.CLIENT_DATA, TransferConstants.TYPE_REQUEST,
                device.toString());
        return transferModel;
    }

    public static ITransferable generateDeviceTransferModelResponse(DeviceDTO device) {
        TransferModel transferModel = new TransferModel(TransferConstants.CLIENT_DATA, TransferConstants.TYPE_RESPONSE,
                device.toString());
        return transferModel;
    }

    public static ITransferable generateDeviceTransferModelRequestWD(DeviceDTO device) {
        TransferModel transferModel = new TransferModel(TransferConstants.CLIENT_DATA_WD, TransferConstants.TYPE_REQUEST,
                device.toString());
        return transferModel;
    }

    public static ITransferable generateDeviceTransferModelResponseWD(DeviceDTO device) {
        TransferModel transferModel = new TransferModel(TransferConstants.CLIENT_DATA_WD, TransferConstants.TYPE_RESPONSE,
                device.toString());
        return transferModel;
    }



    static class TransferModel implements ITransferable {

        int reqCode;
        String reqType;
        String data;

        TransferModel(int reqCode, String reqType, String data) {
            this.reqCode = reqCode;
            this.reqType = reqType;
            this.data = data;
        }

        @Override
        public int getRequestCode() {
            return reqCode;
        }

        @Override
        public String getRequestType() {
            return reqType;
        }

        @Override
        public String getData() {
            return data;
        }
    }
}
