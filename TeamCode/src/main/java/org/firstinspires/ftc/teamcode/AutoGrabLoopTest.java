package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.sections.Lifters;
import org.firstinspires.ftc.teamcode.sections.MecanumDrive;


@Autonomous(name = "Auto Grab Test Loop", group = "Auto Testing")
public final class AutoGrabLoopTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d beginPose = new Pose2d(0, 0, Math.PI/2);
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);
        Lifters lift = new Lifters(hardwareMap);
        drive.setTeamRed();
        waitForStart();
        Actions.runBlocking(
            new SequentialAction(
                drive.actionBuilder(beginPose)
                    .strafeTo(new Vector2d(0,6))
                .build(),
                drive.autoGrabLoop()
            )
        );
    }

}
