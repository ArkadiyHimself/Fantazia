package net.arkadiyhimself.fantazia.client.renderers.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

public class SimpleChasingProjectileModel extends Model {

    private final ModelPart body;

    public SimpleChasingProjectileModel(ModelPart root) {
        super(RenderType::entitySolid);
        this.body = root.getChild("body");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

       partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(12, 0).addBox(-2.0F, -1.0F, 1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(12, 4).addBox(-2.0F, -1.0F, -3.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-2.0F, 2.0F, -2.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 4).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 8).addBox(1.0F, -1.0F, -2.0F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(8, 8).addBox(-3.0F, -1.0F, -2.0F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1F, 0.0F));

       return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
