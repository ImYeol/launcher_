/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\eclipse-SDK-4.2-win32-x86_64\\eclipse\\workspace\\launcher_\\launcher\\src\\com\\example\\Voice\\IVoiceListenerCallback.aidl
 */
package com.example.Voice;
public interface IVoiceListenerCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.example.Voice.IVoiceListenerCallback
{
private static final java.lang.String DESCRIPTOR = "com.example.Voice.IVoiceListenerCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.example.Voice.IVoiceListenerCallback interface,
 * generating a proxy if needed.
 */
public static com.example.Voice.IVoiceListenerCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.example.Voice.IVoiceListenerCallback))) {
return ((com.example.Voice.IVoiceListenerCallback)iin);
}
return new com.example.Voice.IVoiceListenerCallback.Stub.Proxy(obj);
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
case TRANSACTION_onBeginSpeech:
{
data.enforceInterface(DESCRIPTOR);
this.onBeginSpeech();
reply.writeNoException();
return true;
}
case TRANSACTION_onEndOfSpeech:
{
data.enforceInterface(DESCRIPTOR);
this.onEndOfSpeech();
reply.writeNoException();
return true;
}
case TRANSACTION_onResultOfSpeech:
{
data.enforceInterface(DESCRIPTOR);
this.onResultOfSpeech();
reply.writeNoException();
return true;
}
case TRANSACTION_onNotCommandError:
{
data.enforceInterface(DESCRIPTOR);
this.onNotCommandError();
reply.writeNoException();
return true;
}
case TRANSACTION_onVoiceCommand:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.onVoiceCommand(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onVoiceCommand_int:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.onVoiceCommand_int(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.example.Voice.IVoiceListenerCallback
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
@Override public void onBeginSpeech() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onBeginSpeech, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onEndOfSpeech() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onEndOfSpeech, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onResultOfSpeech() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onResultOfSpeech, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onNotCommandError() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onNotCommandError, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onVoiceCommand(java.lang.String command) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(command);
mRemote.transact(Stub.TRANSACTION_onVoiceCommand, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onVoiceCommand_int(int cmdId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(cmdId);
mRemote.transact(Stub.TRANSACTION_onVoiceCommand_int, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onBeginSpeech = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onEndOfSpeech = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onResultOfSpeech = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_onNotCommandError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_onVoiceCommand = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_onVoiceCommand_int = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
}
public void onBeginSpeech() throws android.os.RemoteException;
public void onEndOfSpeech() throws android.os.RemoteException;
public void onResultOfSpeech() throws android.os.RemoteException;
public void onNotCommandError() throws android.os.RemoteException;
public void onVoiceCommand(java.lang.String command) throws android.os.RemoteException;
public void onVoiceCommand_int(int cmdId) throws android.os.RemoteException;
}
