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
public final class BlueBucket extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d beginPose = new Pose2d(60, 60, Math.PI/4);
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);
        Lifters lift = new Lifters(hardwareMap);
        drive.setTeamBlue();
        waitForStart();
        Actions.runBlocking(
                drive.actionBuilder(beginPose)
                        .strafeTo(new Vector2d(60,58))
                        .build()
        );
    }

}
