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
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.sections.Intake;
import org.firstinspires.ftc.teamcode.sections.Lifters;
import org.firstinspires.ftc.teamcode.sections.MecanumDrive;

import java.util.*;

@TeleOp(name = "Tele-OP")
public class TeleOpPlus extends LinearOpMode {
    private FtcDashboard dash = FtcDashboard.getInstance();
    private List<Action> runningActions = new ArrayList<>();
    private List<Action> runningActionsLift = new ArrayList<>();
    boolean driverOveride = false;
    MecanumDrive drive;
    Lifters lift;
    Intake intk;
    @Override
    public void runOpMode(){
        drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));
        lift = new Lifters(hardwareMap);
        intk = new Intake(hardwareMap);

        drive.setTeamRed();
        waitForStart();
        runningActionsLift.add(lift.lifterHold());
        //drive.readPos();
        while(opModeIsActive()){
            looping();
        }
        lift.lifterHoldOff();
    }


    public void looping() {
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

        List<Action> newActions2 = new ArrayList<>();
        for (Action action : runningActionsLift) {
            action.preview(packet.fieldOverlay());
            if (action.run(packet)) {
                newActions.add(action);
            }
        }
        runningActionsLift = newActions2;


//        if (gamepad1.a) {
//            driverOveride = true;
//            runningActions.add( new SequentialAction(
//                    new InstantAction(() -> driverOveride = true),
//                    new InstantAction(() -> Actions.runBlocking(drive.actionBuilder(pose).strafeToLinearHeading(new Vector2d(0, 0),pose.heading.toDouble()).build())),
//                    new InstantAction(() -> driverOveride = false)
//
//            ));
//        }else if(gamepad1.b){
//            driverOveride = true;
//            imageRecMove();
//        }else if(gamepad1.y) {
//            driverOveride = true;
//            drive.autoGrabTest();
//        }else driverOveride = false;
        telemetry.addData("Driver Overide: ", driverOveride);
        telemetry.addData("X: ", drive.pose.position.x);
        telemetry.addData("Y: ", drive.pose.position.y);
        telemetry.addData("Heading: ", drive.pose.heading.toDouble());
        telemetry.addData("Obj X: ", drive.getObjX());
        telemetry.addData("Obj Y: ", drive.getObjY());
        telemetry.addData("Obj Rot: ", drive.getObjRot());
        telemetry.addData("Lifter R: ", lift.vertLifterR.getCurrentPosition());
        telemetry.addData("Lifter L: ", lift.vertLifterL.getCurrentPosition());
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
                double brakeCoeff = 1-gamepad1.right_trigger;
                drive.TeleOpMove(new PoseVelocity2d(new Vector2d(-gamepad1.left_stick_y*brakeCoeff, -gamepad1.left_stick_x*brakeCoeff), -gamepad1.right_stick_x*brakeCoeff));
            }
        }

        //gamepad 2
        if(gamepad2.a){
            intk.intakeIn();
        } else if(gamepad2.b) {
            intk.intakeOut();
        }else intk.intakeOff();

        //lifter control code
        lift.setHorLifterPower(-gamepad2.right_stick_y);

        if(Math.abs(-gamepad2.right_trigger+gamepad2.left_trigger)>0.01){
            lift.setVertLifterPower(-gamepad2.right_trigger+gamepad2.left_trigger);
        }else{
            lift.lifterOverideOff();
        }

        if(gamepad2.y) {
            runningActions.add(lift.setVertLifterPos(450, .5));
        }
        else if(gamepad2.x){
            runningActions.add(lift.setVertLifterPos(2500, .5));
        }
        else{
            lift.vertLifterR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            lift.vertLifterL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            lift.lifterOverideOff();
        }

        dash.sendTelemetryPacket(packet);
    }
    public void imageRecMove(){
            double x = drive.getObjX();
            double xChange = Math.min(Math.max(.005*(130-x),-.5),.5);
            PoseVelocity2d vel = new PoseVelocity2d(new Vector2d(0,xChange),0);
            drive.TeleOpMove(vel);

    }
}