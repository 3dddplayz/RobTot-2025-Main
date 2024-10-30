package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.sections.Lifters;

import org.firstinspires.ftc.teamcode.sections.MecanumDrive;

@Autonomous(name = "Drive Test", group = "Auto Testing")
public final class DriveTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d beginPose = new Pose2d(0, 0, 0);
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);
        Lifters lift = new Lifters(hardwareMap);
        drive.setTeamBlue();
        waitForStart();

        Actions.runBlocking(
            new SequentialAction(
                new ParallelAction(
                    lift.setVertLifterPos(0,.5),
                    drive.actionBuilder(beginPose)
                        .splineTo(new Vector2d(20, 20), Math.PI / 2)
                        .build()
                ),
                new InstantAction(() -> lift.setVertLifterPower(0))
            )
        );
    }
}
