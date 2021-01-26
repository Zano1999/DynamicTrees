package com.ferreusveritas.dynamictrees.models.loaders;

import com.ferreusveritas.dynamictrees.models.geometry.BranchBlockModelGeometry;
import com.ferreusveritas.dynamictrees.models.geometry.CactusBranchBlockModelGeometry;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelLoader;

/**
 * @author Harley O'Connor
 */
@OnlyIn(Dist.CLIENT)
public class CactusBlockModelLoader extends BranchBlockModelLoader {

    @Override
    public BranchBlockModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        final JsonObject textures = this.getTexturesObject(modelContents);

        return this.getModelGeometry(this.getBarkResLoc(textures), this.getRingsResLoc(textures));
    }

    private BranchBlockModelGeometry getModelGeometry (final ResourceLocation barkResLoc, final ResourceLocation ringsResLoc) {
        return new CactusBranchBlockModelGeometry(barkResLoc, ringsResLoc);
    }

}
