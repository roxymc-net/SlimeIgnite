package net.roxymc.slime.ignite.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

import java.io.IOException;

public final class AdventureUtils {
    private AdventureUtils() {
    }

    public static CompoundBinaryTag asAdventure(CompoundTag tag) {
        try {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            NbtIo.write(tag, out);

            return BinaryTagIO.reader().read(ByteStreams.newDataInput(out.toByteArray()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static CompoundTag asVanilla(CompoundBinaryTag tag) {
        try {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            BinaryTagIO.writer().write(tag, out);

            return NbtIo.read(ByteStreams.newDataInput(out.toByteArray()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
