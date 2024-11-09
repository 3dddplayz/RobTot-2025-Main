package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.sections.Lifters;
import org.firstinspires.ftc.teamcode.sections.MecanumDrive;


@Autonomous(name = "HP Auto", group = "Auto Testing")
public final class HumanPlayerAuto extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d beginPose = new Pose2d(24, -60, Math.PI/2);
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);
        Lifters lift = new Lifters(hardwareMap);
        drive.setTeamBlue();
        waitForStart();
        Actions.runBlocking(
            new SequentialAction(
                drive.actionBuilder(beginPose)
                        .strafeTo(new Vector2d(8,-24-12))
                        .strafeTo(new Vector2d(8,-24-10))


                        //push 1 red to HP area
//                        .strafeTo(new Vector2d(8,-24-15))
//                        .strafeTo(new Vector2d(32,-24-15))
//                        .strafeTo(new Vector2d(32,-24-15))
//                        .setTangent(new Rotation2d(0,1))
//                        .splineToLinearHeading(new Pose2d(40,-8,3*Math.PI/2),Math.PI/2)
//
//                        .strafeToLinearHeading(new Vector2d(47,-8),-Math.PI/2)
//                        .strafeTo(new Vector2d(47,-24*3+11))
//                        .strafeToLinearHeading(new Vector2d(8,-24-10),Math.PI/2)
//                        .strafeTo(new Vector2d(8,-24-8))

                        //regrab 1

                        .setTangent(-Math.PI/3)
                        .splineToLinearHeading(new Pose2d(47,-24*3+15,-Math.PI/2),-Math.PI/4)
                        .strafeTo(new Vector2d(47,-24*3+11))

                        //regrab 2
                        .strafeToLinearHeading(new Vector2d(8,-24-10),Math.PI/2)
                        .strafeTo(new Vector2d(8,-24-8))
                        .setTangent(-Math.PI/3)
                        .splineToLinearHeading(new Pose2d(47,-24*3+15,-Math.PI/2),-Math.PI/4)
                        .strafeTo(new Vector2d(47,-24*3+11))

                        //regrab 3
                        .strafeToLinearHeading(new Vector2d(8,-24-10),Math.PI/2)
                        .strafeTo(new Vector2d(8,-24-8))
                        .setTangent(-Math.PI/3)
                        .splineToLinearHeading(new Pose2d(47,-24*3+15,-Math.PI/2),-Math.PI/4)
                        .strafeTo(new Vector2d(47,-24*3+11))

                        //regrab 4
                        .strafeToLinearHeading(new Vector2d(8,-24-10),Math.PI/2)
                        .strafeTo(new Vector2d(8,-24-8))
                        .setTangent(-Math.PI/3)
                        .splineToLinearHeading(new Pose2d(47,-24*3+15,-Math.PI/2),-Math.PI/4)
                        .strafeTo(new Vector2d(47,-24*3+11))
                        .build()
            )
        );
    }

}
