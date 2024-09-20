package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.*;
import java.util.*;

import org.opencv.core.Core;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;

@Autonomous(name = "CameraTest")
public class CameraDetect extends LinearOpMode {
    OpenCvWebcam cam;
    CameraDetectPipeline pipeline;

    @Override
    public void runOpMode() {
        /**
         * NOTE: Many comments have been omitted from this sample for the
         * sake of conciseness. If you're just starting out with EasyOpenCv,
         * you should take a look at {@link InternalCamera2Example} or its
         * webcam counterpart, {@link WebcamExample} first.
         */


        // Create camera instance
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        WebcamName webcamName = hardwareMap.get(WebcamName.class, "webcam");
        cam = OpenCvCameraFactory.getInstance().createWebcam(webcamName, cameraMonitorViewId);

        pipeline = new CameraDetectPipeline();

        // Open async and start streaming inside opened callback
        cam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                cam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);


                cam.setPipeline(pipeline);

                telemetry.addData("rotation", pipeline.getRotation());
                telemetry.addData("x", pipeline.getX());
                telemetry.addData("y", pipeline.getY());
                telemetry.update();
            }

            @Override
            public void onError(int errorCode) {
                telemetry.addData("Camera Error", "Error code: " + errorCode);
                telemetry.update();
            }
        });

        // Tell telemetry to update faster than the default 250ms period :)
        telemetry.setMsTransmissionInterval(20);
        while (!opModeIsActive()) {
        }
        waitForStart();

        while (opModeIsActive()) {

            telemetry.addData("rotation", pipeline.getRotation());
            telemetry.addData("x", pipeline.getX());
            telemetry.addData("y", pipeline.getY());
            telemetry.update();
        }
    }
}
class CameraDetectPipeline extends OpenCvPipeline
{
    double rotation = 0;
    double x = 0;
    double y = 0;

    @Override
    public Mat processFrame(Mat input)
    {

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.cvtColor(input, hierarchy, Imgproc.COLOR_RGB2HSV);

//        // Define range for blue color in HSV
        Scalar lowerBlue = new Scalar(100, 50, 50);
        Scalar upperBlue = new Scalar(130, 255, 255);
//
//        // Create a mask for blue color
        Mat mask = new Mat();
        Core.inRange(hierarchy, lowerBlue, upperBlue, mask);

        Mat blueOnly = new Mat();
        Core.bitwise_and(input, input, blueOnly, mask);

//      Insert Code for Rectangle detections here
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        List<RotatedRect> minRect = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            if (Imgproc.contourArea(contour) > 100) { // Adjust this threshold as needed
                RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contour.toArray()));
                minRect.add(rect);
            }
        }

        // Draw rectangles and get data
        for (int i = 0; i < minRect.size(); i++) {
            Point[] rectPoints = new Point[4];
            minRect.get(i).points(rectPoints);
            for (int j = 0; j < 4; j++) {
                Imgproc.line(blueOnly, rectPoints[j], rectPoints[(j+1) % 4], new Scalar(0, 255, 0), 2);
            }

            // Get rotation, x, and y for the first rectangle (if any)
            if (i == 0) {
                rotation = minRect.get(0).angle;
                x = minRect.get(0).center.x;
                y = minRect.get(0).center.y;
                // You can use these values as needed
            }
        }
        return blueOnly;
    }

    public double getRotation() {
        return rotation;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}