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
        drive.pauseCamera();
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
                                        .splineToConstantHeading(new Vector2d(3,-24-9.45),3*Math.PI/4,new TranslationalVelConstraint(20))
                                        .build(),
                                new SequentialAction(
                                        lift.setVertLifterPos(2520,1)
                                )
                        ),
                        //1st Place on Pole
                        lift.setVertLifterPos(1500,1),
                        intk.IntakeOut(),
                        //Drag Samples to Human Player && Approach Human Player
                        new ParallelAction(
                                drive.actionBuilder(new Pose2d(3, -24-9.45, Math.PI/2))
                                        .setTangent(-Math.PI/4)
                                        .splineToSplineHeading(new Pose2d(30,-36,3*Math.PI/2),Math.PI/6)
                                        .splineToConstantHeading(new Vector2d(38,-10),Math.PI/2)
                                        .splineToConstantHeading(new Vector2d(47,-10),-Math.PI/2,new TranslationalVelConstraint(17))
                                        .strafeToConstantHeading(new Vector2d(47,-24*3+13+7))

                                        .setTangent(Math.PI/2)
                                        .splineToConstantHeading(new Vector2d(47,-13),Math.PI/2)

                                        .splineToConstantHeading(new Vector2d(58,-13),-Math.PI/2,new TranslationalVelConstraint(25))
                                        .splineToConstantHeading(new Vector2d(58,-24*3+13+12),-Math.PI/2)
                                        .splineToConstantHeading(new Vector2d(58,-24*3+13),-Math.PI/2,new TranslationalVelConstraint(30))
                                        .build(),
                                new SequentialAction(
                                        new SleepAction(.75),
                                        lift.setVertLifterPos(660,1),
                                        drive.waitForVector(23.4*2,-23.4*2),
                                        intk.IntakeIn()
                                )
                        ),
                        //Lift Up 2nd Specimen
                        lift.setVertLifterPos(850,1),
                        //Aproach Pole
                        new ParallelAction(
                                drive.actionBuilder(new Pose2d(58,-24*3+13,3*Math.PI/2))
                                        .setTangent(4.5*Math.PI/6)
                                        .splineToSplineHeading(new Pose2d(5.5,-24-9.45,-3*Math.PI/2),2*Math.PI/3,new TranslationalVelConstraint(25))
                                        .build(),
                                new SequentialAction(
                                        intk.IntakeOff(),
                                        lift.setVertLifterPos(2520,1)
                                )
                        ),
                        //2nd Place on Pole
                        lift.setVertLifterPos(1500,1),
                        intk.IntakeOut(),
                        //Approach Human Player
                        new ParallelAction(
                                drive.actionBuilder(new Pose2d(5.5,-24-9.45,Math.PI/2))
                                        .setTangent(-Math.PI/6)
                                        .splineToSplineHeading(new Pose2d(43,-24*3+23,3*Math.PI/2),-Math.PI/6)
                                        .splineToConstantHeading(new Vector2d(47,-24*3+12),-Math.PI/2,new TranslationalVelConstraint(15))
                                        .build(),
                                new SequentialAction(
                                        new SleepAction(.75),
                                        lift.setVertLifterPos(680,1),
                                        intk.IntakeIn()
                                )
                        ),
                        //Lift Up Specimen
                        new SleepAction(.3),
                        lift.setVertLifterPos(850,1),
                        //Aproach Pole
                        new ParallelAction(
                                drive.actionBuilder(new Pose2d(47,-24*3+12,3*Math.PI/2))
                                        .setTangent(2*Math.PI/3)
                                        .splineToSplineHeading(new Pose2d(7.5,-24-9.45,-3*Math.PI/2),2*Math.PI/3,
                                                new TranslationalVelConstraint(25))
                                        .build(),
                                new SequentialAction(
                                        intk.IntakeOff(),
                                        lift.setVertLifterPos(2520,1)
                                )
                        ),
                        //3rd Place on Pole
                        lift.setVertLifterPos(1500,1),
                        intk.IntakeOut(),
                        //Approach Human Player
                        new ParallelAction(
                                drive.actionBuilder(new Pose2d(7.5,-24-9.45,Math.PI/2))
                                        .setTangent(-Math.PI/6)
                                        .splineToSplineHeading(new Pose2d(43,-24*3+23,3*Math.PI/2),-Math.PI/6)
                                        .splineToConstantHeading(new Vector2d(47,-24*3+11.85),-Math.PI/2,new TranslationalVelConstraint(15))
                                        .build(),
                                new SequentialAction(
                                        new SleepAction(.75),
                                        lift.setVertLifterPos(680,1),
                                        intk.IntakeIn()
                                )
                        ),
                        //Lift Up Specimen
                        new SleepAction(.3),
                        lift.setVertLifterPos(1000,1),
//                        lift.setVertLifterPos(850,1),
//                        //Aproach Pole
//                        new ParallelAction(
//                                drive.actionBuilder(new Pose2d(47,-24*3+13,3*Math.PI/2))
//                                        .setTangent(2*Math.PI/3)
//                                        .splineToSplineHeading(new Pose2d(9.5,-24-9.7,-3*Math.PI/2),2*Math.PI/3,
//                                                new TranslationalVelConstraint(25))
//                                        .build(),
//                                new SequentialAction(
//                                        intk.IntakeOff(),
//                                        lift.setVertLifterPos(2520,1)
//                                )
//                        ),
//                        //3rd Place on Pole
//                        lift.setVertLifterPos(1700,1),

                        intk.SetTrunkPos(190),
                        lift.setVertLifterPos(0,1)

                )
        );
    }

}
