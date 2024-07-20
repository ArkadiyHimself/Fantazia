package net.arkadiyhimself.fantazia.client.Models.Entity.NewEntitites.Hatchet;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class HatchetModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Fantazia.res("hatchet"), "main");
	private final ModelPart hatchet;
	public HatchetModel(ModelPart root) {
		this.hatchet = root.getChild("hatchet");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition hatchet = partdefinition.addOrReplaceChild("hatchet", CubeListBuilder.create(), PartPose.offsetAndRotation(3.0F, 6.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition handle = hatchet.addOrReplaceChild("handle", CubeListBuilder.create().texOffs(6, 16).addBox(-6.0F, 0.0F, 6.0F, 1.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(19, 7).addBox(-6.0F, -5.0F, 8.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(20, 0).addBox(-6.0F, -5.0F, 7.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(20, 20).addBox(-6.0F, -10.0F, 7.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 1.0F, -10.0F));

		PartDefinition blade = hatchet.addOrReplaceChild("blade", CubeListBuilder.create().texOffs(10, 0).addBox(2.0F, -1.0F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(12, 16).addBox(1.0F, -1.0F, -1.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(10, 0).addBox(2.0F, 0.0F, -3.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(8, 8).addBox(1.0F, -2.0F, 2.0F, 3.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(1.0F, -2.0F, 4.0F, 3.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 10).addBox(2.0F, -2.0F, 6.0F, 1.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 21).addBox(2.0F, -2.0F, 8.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -7.0F, -2.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		hatchet.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}