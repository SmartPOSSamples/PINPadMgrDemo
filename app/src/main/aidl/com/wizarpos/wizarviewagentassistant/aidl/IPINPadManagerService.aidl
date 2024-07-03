package com.wizarpos.wizarviewagentassistant.aidl;
interface IPINPadManagerService{
    boolean resetMasterKey(int slot);
    boolean resetTransferKey(int slot);
}