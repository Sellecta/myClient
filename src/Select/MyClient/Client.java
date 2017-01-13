package Select.MyClient;

import java.io.*;
import java.net.Socket;

/**
 * Created by Select on 04.01.2017.
 */
public class Client {
    private String ip;
    private int port;
    private byte[] buffer;
    private byte[] bufferMassage;
    private int Length;
    private Socket MySocket;
    private DataInputStream MyDataInp;
    private DataInputStream MyDataInFiles;
    private DataOutputStream MyDataOup;
    private boolean IsOn;
    private Long LengthMessage;
    private File SendFile;


    public Client(String ip,int port){
        try {
            bufferMassage=new byte[512];
            IsOn=true;
            this.ip=ip;
            this.port=port;
            MySocket=new Socket(this.ip,this.port);
            MyDataInp=new DataInputStream(MySocket.getInputStream());
            MyDataOup=new DataOutputStream(MySocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ConnectType(String connectType){

        try {
            MyDataOup.writeUTF(connectType);
            MyDataOup.flush();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                MySocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void SenderMassage(String nick){
        try {
            new Thread(()->{
                while (IsOn){
                    try {
                        LengthMessage=MyDataInp.readLong();
                        buffer=new byte[Math.toIntExact(LengthMessage)];
                        MyDataInp.read(buffer,0, Math.toIntExact(LengthMessage));
                        if(new String(buffer).contains("//sendFiles")){
                            new DownFiles(MySocket);
                        }else System.out.print(new String(buffer));
                    } catch (IOException e) {
                        e.printStackTrace();
                        IsOn=false;
                    }
                }
            }).start();
            MyDataOup.writeUTF(nick);
            MyDataOup.flush();
            while (IsOn){
                Length=System.in.read(bufferMassage);
                if(new String(bufferMassage).contains("//sendFiles"))
                {
                    SendMessage();
                    SendFilesUser();
                }
                else{
                    SendMessage();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            IsOn=false;
        }

    }
    protected  void SendMessage(){
        try{
        MyDataOup.writeLong(Length);
        MyDataOup.write(bufferMassage,0,Length);
        MyDataOup.flush();  }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }


    protected void SendFilesUser(){
        try {
            System.out.println(" Pls enter path files");
            Length=System.in.read(bufferMassage);
            //System.out.println(new String(bufferMassage,0,Length-1));
            SenderFiles(new String(bufferMassage,0,Length-1));
        }catch (IOException ex){
            ex.printStackTrace();
        }

    }

    public void SenderFiles(String Path){
        try{
            SendFile=new File(Path);
            String fileName=SendFile.getName();
            MyDataInFiles=new DataInputStream(new FileInputStream(SendFile));
            MyDataOup.writeLong(MyDataInFiles.available());
            MyDataOup.writeUTF(fileName);
            buffer=new byte[MyDataInFiles.available()];
            MyDataInFiles.read(buffer);
            MyDataOup.write(buffer);
            MyDataOup.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}