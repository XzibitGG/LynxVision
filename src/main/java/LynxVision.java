import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LynxVision {

    public static void main(String[] args) throws IOException {
        //Load DLLs
        loadDLLs();

        //Example "frame" from camera
        Mat frame = Imgcodecs.imread("test_images\\LoadingBay.jpg");


        //Start vision thread
        Thread visionThread = new Thread(() ->{
            //Start network tables
            NetworkTableInstance NTinstance = NetworkTableInstance.getDefault();
            NTinstance.startServer();

            //Get configs from Smartdashboard
            LynxConfig settings = new LynxConfig(NTinstance);

            //Holds all pipeline outputs to switch between
            List<LynxCameraServer> frames = new ArrayList<>();

            //Start new camera feed
            LynxCameraServer rawFrame = new LynxCameraServer("Frame");
            frames.add(rawFrame);

            //LynxPipeline is responsible for all image processing
            LynxPipeline pipeline = new LynxPipeline(settings, frames);

            //Continue while thread is not interrupted
            while(!Thread.interrupted()){
                rawFrame.putFrame(frame);

                //Grab settings again if they have changed
                settings.grabSettings();

                //Process Frame
                pipeline.process(frame);


            }

        });
        visionThread.start();
    }


    public static void loadDLLs() throws IOException {
        //Load dependent libraries
        System.loadLibrary("wpiHaljni");
        System.loadLibrary("wpiHal");
        System.loadLibrary("ntcore");
        System.loadLibrary("ntcorejni");
        System.loadLibrary("opencv_java401");
    }


}