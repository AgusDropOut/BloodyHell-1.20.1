package net.agusdropout.bloodyhell.util.bones;

import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Method;

public final class AgeableModelParts {
    private static final Method HEAD_PARTS = ObfuscationReflectionHelper.findMethod(AgeableListModel.class, "m_5607_");
    private static final Method BODY_PARTS = ObfuscationReflectionHelper.findMethod(AgeableListModel.class, "m_5608_");

    private AgeableModelParts() {}

    public static Iterable<ModelPart> head(AgeableListModel<?> model) {
        return invoke(HEAD_PARTS, model);
    }

    public static Iterable<ModelPart> body(AgeableListModel<?> model) {
        return invoke(BODY_PARTS, model);
    }

    @SuppressWarnings("unchecked")
    private static Iterable<ModelPart> invoke(Method method, AgeableListModel<?> model) {
        try {
            return (Iterable<ModelPart>) method.invoke(model);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot read the parts of " + model.getClass().getName(), e);
        }
    }
}
