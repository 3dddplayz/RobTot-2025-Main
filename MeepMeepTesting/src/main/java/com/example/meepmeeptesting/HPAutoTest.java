package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;
//import com.acmerobotics.roadrunner.ftc.Actions;

public class HPAutoTest {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(700);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(42, 42, Math.toRadians(180), Math.toRadians(180), 15)
                .setDimensions(15,18)
                .build();

        myBot.runAction(
            new SequentialAction(
                myBot.getDrive().actionBuilder(new Pose2d(24, -60, Math.PI/2))
                        .setTangent(3*Math.PI/4)
                        .splineToConstantHeading(new Vector2d(3,-24-10),3*Math.PI/4,new TranslationalVelConstraint(25))
                .build(),
                //first place
                    new SleepAction(.5),
                myBot.getDrive().actionBuilder(new Pose2d(3, -24-10, Math.PI/2))
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
                //second grab
                myBot.getDrive().actionBuilder(new Pose2d(47,-24*3+13,3*Math.PI/2))
                        .setTangent(4.5*Math.PI/6)
                        .splineToSplineHeading(new Pose2d(5.5,-24-10,-3*Math.PI/2),2*Math.PI/3)
                .build(),
                //second place
                    new SleepAction(.5),
                 myBot.getDrive().actionBuilder(new Pose2d(5.5,-24-10,Math.PI/2))
                         .setTangent(-Math.PI/6)
                         .splineToSplineHeading(new Pose2d(43,-24*3+17,3*Math.PI/2),-Math.PI/6)
                         .splineToConstantHeading(new Vector2d(47,-24*3+13),-Math.PI/2)
                .build(),
                //Third Grab
                myBot.getDrive().actionBuilder(new Pose2d(47,-24*3+13,3*Math.PI/2))
                        .setTangent(2*Math.PI/3)
                        .splineToSplineHeading(new Pose2d(7.5,-24-10,-3*Math.PI/2),2*Math.PI/3)
                .build(),
                //Third Place
                new SleepAction(.5),
                myBot.getDrive().actionBuilder(new Pose2d(7.5,-24-10,Math.PI/2))
                        .setTangent(-Math.PI/6)
                        .splineToSplineHeading(new Pose2d(43,-24*3+17,3*Math.PI/2),-Math.PI/6)
                        .splineToConstantHeading(new Vector2d(47,-24*3+13),-Math.PI/2)

                .build(),
                //Fourth Grab
                myBot.getDrive().actionBuilder(new Pose2d(47,-24*3+13,3*Math.PI/2))
                        .setTangent(4.5*Math.PI/6)
                        .splineToSplineHeading(new Pose2d(9.5,-24-10,-3*Math.PI/2),2*Math.PI/3)
                .build(),
                new SleepAction(1)
                //Fourth Place
            )
        );

        meepMeep.setBackground(MeepMeep.Background.FIELD_INTO_THE_DEEP_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}