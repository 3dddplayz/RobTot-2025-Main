package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.sections.Lifters;
import org.firstinspires.ftc.teamcode.sections.MecanumDrive;


@Autonomous(name = "Auto Hope", group = "Auto Testing")
public final class hang extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d beginPose = new Pose2d(-24, 60, -Math.PI/2);
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);
        Lifters lift = new Lifters(hardwareMap);
        drive.setTeamBlue();
        waitForStart();
        Actions.runBlocking(
                new SequentialAction(
                drive.actionBuilder(beginPose)
                        .strafeTo(new Vector2d(-8,24+8))
                        .strafeTo(new Vector2d(-8,24+15))
                        .strafeTo(new Vector2d(-36,24+15))
                        .strafeTo(new Vector2d(-36,5))
                        .strafeTo(new Vector2d(-46,5))
                        .strafeTo(new Vector2d(-46,52))
                        .strafeTo(new Vector2d(-46,5))
                        .strafeTo(new Vector2d(-55,5))
                        .strafeTo(new Vector2d(-55,52))
                        .strafeTo(new Vector2d(-55,5))
                        .strafeTo(new Vector2d(-60,5))
                        .strafeTo(new Vector2d(-60,52))
                        .build()
                )
        );
    }

}
