package net.arkadiyhimself.fantazia.api.attachment.basis_attachments;

public class ReflectLayerRenderHolder {

    public float layerSize = 1f;
    public float layerTransparency = 1f;
    public boolean showLayer = false;

    public void tick() {
        layerSize = Math.min(3f, layerSize + 0.25f);
        layerTransparency = Math.max(0, layerTransparency - 0.05f);
        if (layerSize == 3f) showLayer = false;
    }

    public void reflect() {
        this.layerSize = 1f;
        this.layerTransparency = 1f;
        this.showLayer = true;
    }
}
