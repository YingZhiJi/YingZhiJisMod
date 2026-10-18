package com.example.examplemod;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class ZomperiumScoutModel<T extends LivingEntity> extends HumanoidModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation("yingzhiji_s_mod", "zomperium_scout"), "main");

    public ZomperiumScoutModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // ===== 头 =====
        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        // ===== 帽子（headwear → hat，移到 root 下） =====
        PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(0, 32).addBox(-5.0F, -9.0F, -4.0F, 10.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F))
                .texOffs(0, 44).addBox(-4.5F, -9.5F, -4.5F, 9.0F, 3.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(0, 56).addBox(-1.0F, -10.0F, -5.0F, 2.0F, 3.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        hat.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 69).addBox(-5.0F, -2.0F, -4.0F, 9.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -5.1875F, -0.8125F, -0.2007F, 0.0F, 0.0F));
        hat.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(34, 41).addBox(-4.0F, -2.0F, -2.0F, 8.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.625F, -4.6875F, 0.2618F, 0.0F, 0.0F));
        hat.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(28, 46).addBox(-4.0F, -4.0F, -1.0F, 8.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, -4.5F, -0.2356F, 0.0F, 0.0F));
        hat.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(28, 32).addBox(-4.0F, -7.0F, -1.0F, 9.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -2.4375F, 5.0625F, 0.2182F, 0.0F, 0.0F));

        // ===== 身体（含背包） =====
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 87).addBox(-4.5F, 8.0F, -2.5F, 9.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(100, 120).addBox(-4.5F, 1.5F, 2.0F, 9.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(100, 99).addBox(-3.0F, 0.5F, 2.5F, 6.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(110, 112).mirror().addBox(-2.4375F, 1.0F, 6.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(110, 112).addBox(1.4375F, 1.0F, 6.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(94, 91).addBox(-5.0F, 2.0F, 3.0F, 11.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(124, 85).mirror().addBox(5.4375F, -18.5F, 4.0F, 1.0F, 24.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

        body.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(116, 106).addBox(0.0F, -6.0F, -4.0F, 1.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.3125F, 5.75F, 1.5F, 0.0F, 0.0F, 0.0349F));
        body.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(116, 106).mirror().addBox(-1.0F, -6.0F, -4.0F, 1.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(4.3125F, 5.75F, 1.5F, 0.0F, 0.0F, -0.0349F));
        body.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(29, 88).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.25F, 11.125F, 0.0F, 0.0F, 0.0F, -0.0436F));
        body.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(15, 56).addBox(-3.0F, -6.0F, -1.0F, 7.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0625F, 6.5F, -1.25F, 0.0175F, 0.0F, 0.0F));
        body.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(37, 70).addBox(-1.0F, -4.0F, -2.0F, 2.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.25F, 13.3125F, -0.5F, 0.0F, 0.0F, -0.0873F));

        // ===== 左臂 =====
        PartDefinition leftArm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(5.0F, 2.0F, 0.0F, 0.0F, 0.0F, -0.0873F));
        leftArm.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(0, 62).addBox(-3.0F, -1.0F, -1.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.25F, -1.25F, 0.0F, 0.0F, 0.0F, 0.0873F));

        // ===== 右臂 =====
        PartDefinition rightArm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0873F));
        rightArm.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(0, 57).mirror().addBox(0.0F, -1.0F, -1.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.25F, -1.25F, 0.0F, 0.0F, 0.0F, -0.0873F));

        // ===== 左腿 =====
        PartDefinition leftLeg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(32, 61).addBox(-1.3125F, 11.125F, -2.75F, 4.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, 12.0F, 0.0F));
        leftLeg.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(35, 57).addBox(-1.0F, -1.0F, 0.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3125F, 11.125F, -2.75F, -0.7854F, 0.0F, 0.0F));
        leftLeg.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(55, 113).addBox(-2.0F, -2.0F, -3.0F, 5.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.75F, 0.5F, 0.75F, -0.0436F, 0.0F, -0.0873F));
        leftLeg.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(19, 113).addBox(-2.0F, -2.0F, -1.0F, 5.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6875F, 0.5F, -0.75F, 0.0436F, 0.0F, -0.0873F));
        leftLeg.addOrReplaceChild("left_leg_r1", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(0.0F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.3375F, 12.125F, 0.0F, 0.0F, 0.0F, -0.0873F));

        // ===== 右腿 =====
        PartDefinition rightLeg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(32, 61).mirror().addBox(-2.6875F, 11.125F, -2.75F, 4.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-1.9F, 12.0F, 0.0F));
        rightLeg.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(35, 57).mirror().addBox(-3.0F, -1.0F, 0.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.3125F, 11.125F, -2.75F, -0.7854F, 0.0F, 0.0F));
        rightLeg.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(37, 113).mirror().addBox(-3.0F, -2.0F, -3.0F, 5.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.75F, 0.5F, 0.75F, -0.0436F, 0.0F, 0.0873F));
        rightLeg.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(0, 104).mirror().addBox(-3.0F, -2.0F, -1.0F, 5.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.6875F, 0.5F, -0.75F, 0.0436F, 0.0F, 0.0873F));
        rightLeg.addOrReplaceChild("right_leg_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.3375F, 12.125F, 0.0F, 0.0F, 0.0F, 0.0873F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.hat.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}