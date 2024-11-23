package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.sections.Intake;
import org.firstinspires.ftc.teamcode.sections.Lifters;
import org.firstinspires.ftc.teamcode.sections.MecanumDrive;


@Autonomous(name = "HP Auto", group = "Auto Testing")
public final class HumanPlayerAuto extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d beginPose = new Pose2d(24, -60, Math.PI/2);
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);
        Lifters lift = new Lifters(hardwareMap);
        Intake intk = new Intake(hardwareMap);
        drive.setTeamBlue();
        waitForStart();
        Actions.runBlocking(
            //main sequential
            new SequentialAction(
                intk.SetTrunkPos(90),
                intk.SetTwistPos(90),
                //Aproach Pole
                new ParallelAction(
                    drive.actionBuilder(beginPose)
                            .setTangent(3*Math.PI/4)
                            .splineToConstantHeading(new Vector2d(3,-24-10),3*Math.PI/4,new TranslationalVelConstraint(25))
                    .build(),
                    new SequentialAction(
                            lift.setVertLifterPos(2520,1)
                    )
                ),
                //1st Place on Pole
                lift.setVertLifterPos(1600,1),
                //Drag Samples to Human Player && Approach Human Player
                new ParallelAction(
                    drive.actionBuilder(new Pose2d(3, -24-10, Math.PI/2))
                            .setTangent(-Math.PI/4)
                            .splineToSplineHeading(new Pose2d(28,-38,3*Math.PI/2),Math.PI/6)
                            .splineToConstantHeading(new Vector2d(44,-13),0)
                            .splineToConstantHeading(new Vector2d(58,-17),-Math.PI/2)
                            .splineToConstantHeading(new Vector2d(58,-24*3+13),-Math.PI/2)
                            .setTangent(Math.PI/2)
                            .splineToConstantHeading(new Vector2d(58,-14),Math.PI/2)
                            .splineToConstantHeading(new Vector2d(47,-14),-Math.PI/2)
                            .splineToConstantHeading(new Vector2d(47,-24*3+13),-Math.PI/2)
                    .build(),
                    new SequentialAction(
                            lift.setVertLifterPos(700,1),
                            drive.waitForVector(23.4*2,-23.4*2),
                            intk.IntakeIn()
                    )
                ),
                //Lift Up 2nd Specimen
                lift.setVertLifterPos(800,1),
                //Aproach Pole
                new ParallelAction(
                    drive.actionBuilder(new Pose2d(47,-24*3+13,3*Math.PI/2))
                            .setTangent(4.5*Math.PI/6)
                            .splineToSplineHeading(new Pose2d(5.5,-24-10,-3*Math.PI/2),2*Math.PI/3)
                    .build(),
                    new SequentialAction(
                            intk.IntakeOff(),
                            lift.setVertLifterPos(2520,1)
                    )
                ),
                //2nd Place on Pole
                lift.setVertLifterPos(1600,1),
                //Approach Human Player
                new ParallelAction(
                    drive.actionBuilder(new Pose2d(5.5,-24-10,Math.PI/2))
                            .setTangent(-Math.PI/6)
                            .splineToSplineHeading(new Pose2d(43,-24*3+17,3*Math.PI/2),-Math.PI/6)
                            .splineToConstantHeading(new Vector2d(47,-24*3+13),-Math.PI/2)
                    .build(),
                    new SequentialAction(
                            lift.setVertLifterPos(700,1),
                            intk.IntakeIn()
                    )
                ),
                //Lift Up Specimen
                lift.setVertLifterPos(800,1),
                //Aproach Pole
                new ParallelAction(
                    drive.actionBuilder(new Pose2d(47,-24*3+13,3*Math.PI/2))
                            .setTangent(2*Math.PI/3)
                            .splineToSplineHeading(new Pose2d(7.5,-24-10,-3*Math.PI/2),2*Math.PI/3)
                    .build(),
                    new SequentialAction(
                            intk.IntakeOff(),
                            lift.setVertLifterPos(2520,1)
                    )
                ),
                //3rd Place on Pole
                lift.setVertLifterPos(1600,1),
                //Approach Human Player
                new ParallelAction(
                    drive.actionBuilder(new Pose2d(5.5,-24-10,Math.PI/2))
                        .setTangent(-Math.PI/6)
                        .splineToSplineHeading(new Pose2d(43,-24*3+17,3*Math.PI/2),-Math.PI/6)
                        .splineToConstantHeading(new Vector2d(47,-24*3+13),-Math.PI/2)
                        .build(),
                new SequentialAction(
                        lift.setVertLifterPos(700,1),
                        intk.IntakeIn()
                )
            ),
                //Lift Up Specimen
                lift.setVertLifterPos(800,1),
                //Aproach Pole
                new ParallelAction(
                    drive.actionBuilder(new Pose2d(47,-24*3+13,3*Math.PI/2))
                            .setTangent(2*Math.PI/3)
                            .splineToSplineHeading(new Pose2d(9.5,-24-10,-3*Math.PI/2),2*Math.PI/3)
                            .build(),
                    new SequentialAction(
                            intk.IntakeOff(),
                            lift.setVertLifterPos(2520,1)
                    )
                ),
                //4rd Place on Pole
                lift.setVertLifterPos(1600,1)

            )
        );
    }

}
