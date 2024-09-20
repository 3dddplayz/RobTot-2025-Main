package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.movement.MecanumDrive;

import org.firstinspires.ftc.teamcode.rrLibs.tuning.TuningOpModes;

@Autonomous(name = "Drive Test", group = "Auto Testing")
public final class DriveTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d beginPose = new Pose2d(0, 0, 0);
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

        waitForStart();
        double x = drive.getObjX();
        Actions.runBlocking(
                drive.actionBuilder(beginPose)
                        .splineToLinearHeading(new Pose2d(x, 0, 0), Math.PI / 2, new TranslationalVelConstraint(5.0))
                        //.strafeToConstantHeading(new Vector2d(0, 0), new TranslationalVelConstraint(5.0))
                        .build());
        drive.writePos();
    }
}
