package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.movement.MecanumDrive;

import java.util.*;

@TeleOp(name = "Tele-OP")
public class TeleOpPlus extends LinearOpMode {
    private FtcDashboard dash = FtcDashboard.getInstance();
    private List<Action> runningActions = new ArrayList<>();
    boolean driverOveride = false;

    @Override
    public void runOpMode(){
        waitForStart();
//        String fileName = "choords.json";
//        File file = AppUtil.getInstance().getSettingsFile(fileName);
//        double x = new Double(ReadWriteFile.readFile(file)).doubleValue();
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));
        //drive.readPos();
        while(opModeIsActive()){
            looping(drive);
        }
    }


    public void looping(MecanumDrive drive) {
        drive.updatePoseEstimate();

        TelemetryPacket packet = new TelemetryPacket();
        Pose2d pose = drive.pose;

        // updated based on gamepads

        // update running actions
        List<Action> newActions = new ArrayList<>();
        for (Action action : runningActions) {
            action.preview(packet.fieldOverlay());
            if (action.run(packet)) {
                newActions.add(action);
            }
        }
        runningActions = newActions;
        if (gamepad1.a) {
            driverOveride = true;
            runningActions.add( new SequentialAction(
                    new InstantAction(() -> driverOveride = true),
                    new InstantAction(() -> Actions.runBlocking(drive.actionBuilder(pose).strafeToLinearHeading(new Vector2d(20+pose.position.x, 20+pose.position.y),pose.heading.toDouble()+Math.PI/2).build())),
                    new InstantAction(() -> driverOveride = false)

            ));
        }else driverOveride = false;
        telemetry.addData("Driver Overide: ", driverOveride);
        telemetry.addData("X: ", drive.pose.position.x);
        telemetry.addData("Y: ", drive.pose.position.y);
        telemetry.addData("Heading: ", drive.pose.heading.toDouble());
        telemetry.update();
        if(!driverOveride) {
            if (gamepad1.dpad_up) {
                drive.TeleOpMove(new PoseVelocity2d(new Vector2d(1, 0), 0));
            } else if (gamepad1.dpad_down) {
                drive.TeleOpMove(new PoseVelocity2d(new Vector2d(-1, 0), 0));
            } else if (gamepad1.dpad_left){
                drive.TeleOpMove(new PoseVelocity2d(new Vector2d(0, 1), 0));
            } else if (gamepad1.dpad_right){
                drive.TeleOpMove(new PoseVelocity2d(new Vector2d(0, -1), 0));
            }
            else {
                drive.TeleOpMove(new PoseVelocity2d(new Vector2d(-gamepad1.left_stick_y, -gamepad1.left_stick_x), -gamepad1.right_stick_x));
            }
        }
        dash.sendTelemetryPacket(packet);
    }
}