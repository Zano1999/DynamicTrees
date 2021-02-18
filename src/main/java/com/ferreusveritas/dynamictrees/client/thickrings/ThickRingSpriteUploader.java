package com.ferreusveritas.dynamictrees.client.thickrings;

import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.EmptyProfiler;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ThickRingSpriteUploader extends ReloadListener<AtlasTexture.SheetData> implements AutoCloseable {

    private final ThickRingAtlasTexture textureAtlas;

    public ThickRingSpriteUploader(TextureManager textureManagerIn) {
        textureAtlas = new ThickRingAtlasTexture();
        textureManagerIn.loadTexture(textureAtlas.getTextureLocation(), textureAtlas);
    }

    public ThickRingAtlasTexture getTextureAtlas (){
        return textureAtlas;
    }

//    public AtlasTexture.SheetData prepare (IResourceManager resourceManager){
//        IProfiler profiler = EmptyProfiler.INSTANCE;
//        profiler.startTick();
//        profiler.startSection("stitching");
//        AtlasTexture.SheetData atlastexture$sheetdata = textureAtlas.stitch(resourceManager, profiler, 0);
//        profiler.endSection();
//        profiler.endTick();
//        return atlastexture$sheetdata;
//    }
//    public void apply(AtlasTexture.SheetData sheetData) {
//        IProfiler profiler = EmptyProfiler.INSTANCE;
//        profiler.startTick();
//        profiler.startSection("upload");
//        textureAtlas.upload(sheetData);
//        profiler.endSection();
//        profiler.endTick();
//    }

    /**
     * Performs any reloading that can be done off-thread, such as file IO
     */
    protected AtlasTexture.SheetData prepare(IResourceManager resourceManager, IProfiler profiler) {
        profiler.startTick();
        profiler.startSection("stitching");
        AtlasTexture.SheetData atlastexture$sheetdata = textureAtlas.stitch(resourceManager, profiler, 0);
        profiler.endSection();
        profiler.endTick();
        return atlastexture$sheetdata;
    }

    protected void apply(AtlasTexture.SheetData sheetData, IResourceManager resourceManager, IProfiler profiler) {
        profiler.startTick();
        profiler.startSection("upload");
        textureAtlas.upload(sheetData);
        profiler.endSection();
        profiler.endTick();
    }

    public void close() {
        textureAtlas.clear();
    }

}