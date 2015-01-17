/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\eclipse-SDK-4.2-win32-x86_64\\eclipse\\workspace\\launcher_\\launcher\\src\\com\\example\\Voice\\IVoiceListenerService.aidl
 */
package com.example.Voice;
public interface IVoiceListenerService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.example.Voice.IVoiceListenerService
{
private static final java.lang.String DESCRIPTOR = "com.example.Voice.IVoiceListenerService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.example.Voice.IVoiceListenerService interface,
 * generating a proxy if needed.
 */
public static com.example.Voice.IVoiceListenerService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.example.Voice.IVoiceListenerService))) {
return ((com.example.Voice.IVoiceListenerService)iin);
}
return new com.example.Voice.IVoiceListenerService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_setCommands:
{
data.enforceInterface(DESCRIPTOR);
com.example.Voice.VoiceCommand _arg0;
if ((0!=data.readInt())) {
_arg0 = com.example.Voice.VoiceCommand.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.setCommands(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_ReSetCommands:
{
data.enforceInterface(DESCRIPTOR);
this.ReSetCommands();
reply.writeNoException();
return true;
}
case TRANSACTION_registerCallback:
{
data.enforceInterface(DESCRIPTOR);
com.example.Voice.IVoiceListenerCallback _arg0;
_arg0 = com.example.Voice.IVoiceListenerCallback.Stub.asInterface(data.readStrongBinder());
this.registerCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unRegisterCallback:
{
data.enforceInterface(DESCRIPTOR);
this.unRegisterCallback();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.example.Voice.IVoiceListenerService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void setCommands(com.example.Voice.VoiceCommand commands) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((commands!=null)) {
_data.writeInt(1);
commands.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_setCommands, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void ReSetCommands() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_ReSetCommands, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void registerCallback(com.example.Voice.IVoiceListenerCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unRegisterCallback() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_unRegisterCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_setCommands = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_ReSetCommands = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_registerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_unRegisterCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public void setCommands(com.example.Voice.VoiceCommand commands) throws android.os.RemoteException;
public void ReSetCommands() throws android.os.RemoteException;
public void registerCallback(com.example.Voice.IVoiceListenerCallback callback) throws android.os.RemoteException;
public void unRegisterCallback() throws android.os.RemoteException;
}
