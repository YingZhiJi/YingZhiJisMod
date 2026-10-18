package com.example.examplemod;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class ZomperiumCommanderModel<T extends LivingEntity> extends HumanoidModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation("yingzhiji_s_mod", "zomperium_commander"), "main");

    public ZomperiumCommanderModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-4.0F, -10.1875F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 1.5F, 0.0F));

        PartDefinition hat = partdefinition.addOrReplaceChild("hat",
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-5.0F, -11.1875F, -4.0F, 10.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(32, 0).addBox(-4.0F, -10.1875F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F))
                        .texOffs(0, 44).addBox(-4.5F, -11.6875F, -4.5F, 9.0F, 3.0F, 9.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 56).addBox(-1.0F, -12.1875F, -5.0F, 2.0F, 3.0F, 10.0F, new CubeDeformation(0.0F))
                        .texOffs(100, 77).addBox(-4.5F, -4.6875F, -0.5F, 9.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
                        .texOffs(99, 45).addBox(1.25F, -3.6875F, -4.25F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 1.5F, 0.0F));

        hat.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(89, 79)
                        .addBox(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -8.0625F, 4.75F, 0.2443F, 0.0F, 0.0F));

        hat.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 69)
                        .addBox(-5.0F, -2.0F, -4.0F, 9.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.5F, -7.375F, -0.8125F, -0.2007F, 0.0F, 0.0F));

        hat.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(34, 41)
                        .addBox(-4.0F, -2.0F, -2.0F, 8.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -5.8125F, -4.6875F, 0.2618F, 0.0F, 0.0F));

        hat.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(28, 46)
                        .addBox(-4.0F, -4.0F, -1.0F, 8.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -7.1875F, -4.5F, -0.2356F, 0.0F, 0.0F));

        hat.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(28, 32)
                        .addBox(-4.0F, -7.0F, -1.0F, 9.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-0.5F, -4.625F, 5.0625F, 0.2182F, 0.0F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(16, 16).addBox(-4.0F, -2.1875F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 87).addBox(-4.5F, 5.8125F, -2.5F, 9.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 1.5F, 0.0F));

        body.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(15, 56)
                        .addBox(-3.0F, -6.0F, -1.0F, 7.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0625F, 4.3125F, -1.25F, 0.0175F, 0.0F, 0.0F));

        PartDefinition bone5 = body.addOrReplaceChild("bone5",
                CubeListBuilder.create().texOffs(116, 117)
                        .addBox(-3.25F, 3.8125F, -2.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-0.5625F, 2.5625F, -1.6875F));

        bone5.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(106, 106)
                        .addBox(-2.0F, -2.0F, -1.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-0.75F, 5.6875F, -1.3125F, -0.0873F, 0.0F, 0.0F));

        PartDefinition bone = body.addOrReplaceChild("bone",
                CubeListBuilder.create()
                        .texOffs(110, 54).addBox(-4.0F, 4.0F, -9.1875F, 7.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(114, 66).addBox(-3.0F, 0.5F, -10.1875F, 5.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.5F, -1.875F, 12.0F));

        bone.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(91, 69)
                        .addBox(-4.0F, -4.0F, -2.0F, 8.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-0.5F, 4.5F, -7.6875F, -0.2094F, 0.0F, 0.0F));

        bone.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(93, 89)
                        .addBox(-4.0F, -2.0F, -1.0F, 8.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-0.5F, 7.5F, -8.375F, 0.1396F, 0.0F, 0.0F));

        bone.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(106, 60)
                        .addBox(-4.0F, -2.0F, -1.0F, 7.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 1.5F, -8.6875F, -0.384F, 0.0F, 0.0F));

        bone.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(113, 88)
                        .addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-0.5F, 2.0625F, -6.75F, -0.4363F, 0.0F, 0.0F));

        PartDefinition bone2 = body.addOrReplaceChild("bone2", CubeListBuilder.create(),
                PartPose.offsetAndRotation(4.9375F, 10.875F, -7.1875F, -0.2899F, 0.0086F, -0.0936F));

        bone2.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(91, 117)
                        .addBox(-1.0701F, -2.7377F, -1.6991F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-0.5F, -3.3125F, 2.0469F, 1.501F, 0.0F, 0.0F));

        bone2.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(91, 117)
                        .addBox(-1.0701F, -2.7377F, -1.6991F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-0.5F, -6.25F, 2.25F, 1.501F, 0.0F, 0.0F));

        bone2.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(74, 99)
                        .addBox(-1.0701F, -2.3009F, -0.7377F, 1.0F, 3.0F, 26.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -2.9375F, 0.0F, -0.0698F, 0.0F, 0.0F));

        PartDefinition bone3 = body.addOrReplaceChild("bone3", CubeListBuilder.create(),
                PartPose.offset(0.0F, 6.0F, -4.0F));

        bone3.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(22, 81)
                        .addBox(-4.0F, 0.0F, 0.0F, 9.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-0.5F, -3.125F, 1.5F, 0.0F, 0.0F, -0.1745F));

        bone3.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(0, 81)
                        .addBox(-4.0F, -2.0F, -1.0F, 8.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -2.1875F, 0.0F, 0.0F, 0.0F, -0.1745F));

        PartDefinition bone4 = body.addOrReplaceChild("bone4", CubeListBuilder.create(),
                PartPose.offset(5.5F, 11.3125F, 0.0F));

        bone4.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(37, 70)
                        .addBox(-1.0F, -4.0F, -2.0F, 2.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, -0.5F, 0.0F, 0.0F, -0.0873F));

        bone4.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(29, 88)
                        .addBox(-1.0F, -2.0F, -2.0F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -2.1875F, 0.0F, 0.0F, 0.0F, -0.0436F));

        PartDefinition bone6 = body.addOrReplaceChild("bone6", CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition jump = bone6.addOrReplaceChild("jump", CubeListBuilder.create(),
                PartPose.offset(0.625F, 0.0F, 0.0F));

        jump.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(122, 28)
                        .addBox(0.0F, -3.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-6.25F, -2.4375F, 3.0F, 0.3491F, 0.0F, 0.2182F));

        jump.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(116, 40)
                        .addBox(-2.0F, -2.0F, -4.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-4.9375F, -3.25F, 7.1875F, 0.7854F, 0.0F, 0.3491F));

        jump.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(110, 28)
                        .addBox(0.0F, -3.0F, -4.0F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-7.5F, -1.125F, 6.5625F, 0.0F, 0.0F, 0.3491F));

        jump.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(110, 28)
                        .addBox(0.0F, -3.0F, -4.0F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-6.1875F, -0.625F, 6.5625F, 0.0F, 0.0F, 0.3491F));

        jump.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(107, 28)
                        .addBox(0.0F, -2.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-5.0625F, -0.1875F, 2.5F, -0.4184F, -0.26F, 0.2922F));

        PartDefinition jump2 = bone6.addOrReplaceChild("jump2", CubeListBuilder.create(),
                PartPose.offset(-0.625F, 0.0F, 0.0F));

        jump2.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(122, 28).mirror()
                        .addBox(-1.0F, -3.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(6.25F, -2.4375F, 3.0F, 0.3491F, 0.0F, -0.2182F));

        jump2.addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(116, 40).mirror()
                        .addBox(-1.0F, -2.0F, -4.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(4.9375F, -3.25F, 7.1875F, 0.7854F, 0.0F, -0.3491F));

        jump2.addOrReplaceChild("cube_r26", CubeListBuilder.create().texOffs(110, 28).mirror()
                        .addBox(-1.0F, -3.0F, -4.0F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(7.5F, -1.125F, 6.5625F, 0.0F, 0.0F, -0.3491F));

        jump2.addOrReplaceChild("cube_r27", CubeListBuilder.create().texOffs(110, 28).mirror()
                        .addBox(-1.0F, -3.0F, -4.0F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(6.1875F, -0.625F, 6.5625F, 0.0F, 0.0F, -0.3491F));

        jump2.addOrReplaceChild("cube_r28", CubeListBuilder.create().texOffs(107, 28).mirror()
                        .addBox(-1.0F, -2.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(5.0625F, -0.1875F, 2.5F, -0.4184F, 0.26F, -0.2922F));

        PartDefinition bone7 = body.addOrReplaceChild("bone7", CubeListBuilder.create(),
                PartPose.offset(0.0F, 9.0F, -2.0F));

        PartDefinition jump4 = bone7.addOrReplaceChild("jump4", CubeListBuilder.create(),
                PartPose.offsetAndRotation(2.0F, -4.625F, 3.6875F, -0.5352F, -0.2263F, -0.0837F));

        jump4.addOrReplaceChild("cube_r29", CubeListBuilder.create().texOffs(122, 28)
                        .addBox(-0.1551F, -2.4969F, -0.4658F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-6.25F, -3.1875F, 3.0F, -0.286F, 0.1914F, -0.0325F));

        jump4.addOrReplaceChild("cube_r30", CubeListBuilder.create().texOffs(116, 40)
                        .addBox(-1.84F, -1.2847F, -4.1591F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-4.9375F, -4.0F, 7.1875F, 0.7854F, 0.0F, 0.3491F));

        jump4.addOrReplaceChild("cube_r31", CubeListBuilder.create().texOffs(110, 28)
                        .addBox(0.16F, -2.3818F, -3.6067F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-7.5F, -1.875F, 6.5625F, 0.0F, 0.0F, 0.3491F));

        jump4.addOrReplaceChild("cube_r32", CubeListBuilder.create().texOffs(110, 28)
                        .addBox(0.16F, -2.3818F, -3.6067F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-6.1875F, -1.375F, 6.5625F, 0.0F, 0.0F, 0.3491F));

        PartDefinition jump3 = bone7.addOrReplaceChild("jump3", CubeListBuilder.create(),
                PartPose.offsetAndRotation(-2.0F, -4.625F, 3.6875F, -0.5352F, 0.2263F, 0.0837F));

        jump3.addOrReplaceChild("cube_r33", CubeListBuilder.create().texOffs(122, 28).mirror()
                        .addBox(-0.8449F, -2.4969F, -0.4658F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(6.25F, -3.1875F, 3.0F, -0.286F, -0.1914F, 0.0325F));

        jump3.addOrReplaceChild("cube_r34", CubeListBuilder.create().texOffs(116, 40).mirror()
                        .addBox(-1.16F, -1.2847F, -4.1591F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(4.9375F, -4.0F, 7.1875F, 0.7854F, 0.0F, -0.3491F));

        jump3.addOrReplaceChild("cube_r35", CubeListBuilder.create().texOffs(110, 28).mirror()
                        .addBox(-1.16F, -2.3818F, -3.6067F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(7.5F, -1.875F, 6.5625F, 0.0F, 0.0F, -0.3491F));

        jump3.addOrReplaceChild("cube_r36", CubeListBuilder.create().texOffs(110, 28).mirror()
                        .addBox(-1.16F, -2.3818F, -3.6067F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(6.1875F, -1.375F, 6.5625F, 0.0F, 0.0F, -0.3491F));

        PartDefinition leftArm = partdefinition.addOrReplaceChild("left_arm",
                CubeListBuilder.create()
                        .texOffs(40, 16).mirror().addBox(-0.4131F, -4.1624F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                        .texOffs(94, 65).addBox(3.4619F, -0.1624F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(99, 55).addBox(3.0244F, -4.6624F, -0.5F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(4.75F, 3.8125F, 0.0F, 0.0F, 0.0F, -0.096F));

        leftArm.addOrReplaceChild("cube_r37", CubeListBuilder.create().texOffs(39, 94).mirror()
                        .addBox(2.5241F, -7.0925F, -1.631F, 1.0F, 11.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(1.875F, 1.625F, -0.5F, 0.1745F, 0.0F, -0.0873F));

        leftArm.addOrReplaceChild("cube_r38", CubeListBuilder.create().texOffs(0, 62)
                        .addBox(-2.8528F, -3.1835F, -1.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(2.5F, -1.25F, 0.0F, 0.0F, 0.0F, 0.0873F));

        PartDefinition rightArm = partdefinition.addOrReplaceChild("right_arm",
                CubeListBuilder.create()
                        .texOffs(40, 16).addBox(-3.3369F, -4.1624F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(94, 65).mirror().addBox(-4.2119F, -0.1624F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
                        .texOffs(99, 55).mirror().addBox(-3.7744F, -4.6624F, -0.5F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(-5.0625F, 3.8125F, 0.0F, 0.0F, 0.0F, 0.096F));

        rightArm.addOrReplaceChild("cube_r39", CubeListBuilder.create().texOffs(0, 57).mirror()
                        .addBox(-0.1472F, -3.1835F, -1.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(-2.25F, -1.25F, 0.0F, 0.0F, 0.0F, -0.0873F));

        rightArm.addOrReplaceChild("cube_r40", CubeListBuilder.create().texOffs(19, 94)
                        .addBox(-3.5241F, -7.0925F, -1.631F, 1.0F, 11.0F, 5.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1.625F, 1.625F, -0.5F, 0.1745F, 0.0F, 0.0873F));

        PartDefinition leftLeg = partdefinition.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(32, 61)
                        .addBox(-1.3125F, 8.9375F, -2.75F, 4.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
                PartPose.offset(1.9F, 13.5F, 0.0F));

        leftLeg.addOrReplaceChild("cube_r41", CubeListBuilder.create().texOffs(35, 57)
                        .addBox(-1.0F, -1.0F, 0.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-0.3125F, 8.9375F, -2.75F, -0.7854F, 0.0F, 0.0F));

        leftLeg.addOrReplaceChild("cube_r42", CubeListBuilder.create().texOffs(55, 113)
                        .addBox(-2.0F, -2.0F, -3.0F, 5.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-0.75F, -1.6875F, 0.75F, -0.0436F, 0.0F, -0.0873F));

        leftLeg.addOrReplaceChild("cube_r43", CubeListBuilder.create().texOffs(19, 113)
                        .addBox(-2.0F, -2.0F, -1.0F, 5.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-0.6875F, -1.6875F, -0.75F, 0.0436F, 0.0F, -0.0873F));

        leftLeg.addOrReplaceChild("left_leg_r1", CubeListBuilder.create().texOffs(0, 16).mirror()
                        .addBox(0.0F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(-1.3375F, 9.9375F, 0.0F, 0.0F, 0.0F, -0.0873F));

        PartDefinition bone9 = leftLeg.addOrReplaceChild("bone9",
                CubeListBuilder.create()
                        .texOffs(112, 12).mirror().addBox(-1.0F, -0.1563F, -4.1094F, 2.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
                        .texOffs(112, 12).addBox(1.1844F, -0.1406F, -4.1094F, 2.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-0.4844F, 9.7813F, 0.8594F));

        bone9.addOrReplaceChild("cube_r44", CubeListBuilder.create().texOffs(109, 0)
                        .addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(2.4969F, -3.4375F, 1.375F, -0.0377F, 1.344F, -1.7523F));

        bone9.addOrReplaceChild("cube_r45", CubeListBuilder.create().texOffs(121, 1)
                        .addBox(-1.0F, -2.0F, 1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(2.1219F, -0.7344F, -1.8594F, 2.7925F, 0.0F, 0.0F));

        bone9.addOrReplaceChild("cube_r46", CubeListBuilder.create().texOffs(121, 1).mirror()
                        .addBox(-1.0F, -2.0F, 1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(0.125F, -0.7344F, -1.9688F, 2.7925F, 0.0F, 0.0F));

        bone9.addOrReplaceChild("cube_r47", CubeListBuilder.create().texOffs(109, 0)
                        .addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-0.1438F, 0.2188F, -0.4375F, 0.0264F, -0.0012F, -0.07F));

        bone9.addOrReplaceChild("cube_r48", CubeListBuilder.create().texOffs(109, 0).mirror()
                        .addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(2.375F, 0.2188F, -0.4375F, 0.0264F, -0.0011F, 0.1572F));

        PartDefinition rightLeg = partdefinition.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(32, 61).mirror()
                        .addBox(-2.6875F, 8.9375F, -2.75F, 4.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(-1.9F, 13.5F, 0.0F));

        rightLeg.addOrReplaceChild("cube_r49", CubeListBuilder.create().texOffs(116, 88)
                        .addBox(-1.0F, -2.0F, -2.0F, 1.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1.4375F, -2.5F, -0.5F, 0.0F, 0.0F, 0.0873F));

        rightLeg.addOrReplaceChild("cube_r50", CubeListBuilder.create().texOffs(116, 88)
                        .addBox(-1.0F, -2.0F, -2.0F, 1.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1.6875F, 0.8125F, -0.5F, 0.0F, 0.0F, 0.0873F));

        rightLeg.addOrReplaceChild("cube_r51", CubeListBuilder.create().texOffs(35, 57).mirror()
                        .addBox(-3.0F, -1.0F, 0.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(0.3125F, 8.9375F, -2.75F, -0.7854F, 0.0F, 0.0F));

        rightLeg.addOrReplaceChild("cube_r52", CubeListBuilder.create().texOffs(37, 113).mirror()
                        .addBox(-3.0F, -2.0F, -3.0F, 5.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(0.75F, -1.6875F, 0.75F, -0.0436F, 0.0F, 0.0873F));

        rightLeg.addOrReplaceChild("cube_r53", CubeListBuilder.create().texOffs(0, 104).mirror()
                        .addBox(-3.0F, -2.0F, -1.0F, 5.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(0.6875F, -1.6875F, -0.75F, 0.0436F, 0.0F, 0.0873F));

        rightLeg.addOrReplaceChild("right_leg_r1", CubeListBuilder.create().texOffs(0, 16)
                        .addBox(-4.0F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.3375F, 9.9375F, 0.0F, 0.0F, 0.0F, 0.0873F));

        PartDefinition bone8 = rightLeg.addOrReplaceChild("bone8",
                CubeListBuilder.create()
                        .texOffs(112, 12).addBox(-1.0F, -0.1563F, -4.1094F, 2.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(112, 12).mirror().addBox(-3.1844F, -0.1406F, -4.1094F, 2.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(0.4844F, 9.7813F, 0.8594F));

        bone8.addOrReplaceChild("cube_r54", CubeListBuilder.create().texOffs(109, 0).mirror()
                        .addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(-2.4969F, -3.4375F, 1.375F, -0.0377F, -1.344F, 1.7523F));

        bone8.addOrReplaceChild("cube_r55", CubeListBuilder.create().texOffs(121, 1).mirror()
                        .addBox(-1.0F, -2.0F, 1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(-2.1219F, -0.7344F, -1.8594F, 2.7925F, 0.0F, 0.0F));

        bone8.addOrReplaceChild("cube_r56", CubeListBuilder.create().texOffs(121, 1)
                        .addBox(-1.0F, -2.0F, 1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-0.125F, -0.7344F, -1.9688F, 2.7925F, 0.0F, 0.0F));

        bone8.addOrReplaceChild("cube_r57", CubeListBuilder.create().texOffs(109, 0).mirror()
                        .addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(0.1438F, 0.2188F, -0.4375F, 0.0264F, 0.0012F, 0.07F));

        bone8.addOrReplaceChild("cube_r58", CubeListBuilder.create().texOffs(109, 0)
                        .addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-2.375F, 0.2188F, -0.4375F, 0.0264F, 0.0011F, -0.1572F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }
}