package net.flectone.cookieclicker.utility.data;

import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3i;
import lombok.Getter;
import net.minecraft.world.phys.Vec3;

@Getter
public class Position {
    private final double x;
    private final double y;
    private final double z;

    public Position(Double x, Double y, Double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Position(Vec3 vec3) {
        this(vec3.x, vec3.y, vec3.z);
    }

    public Position(Location location) {
        this(location.getX(), location.getY(), location.getZ());
    }

    public Position(Vector3i vector3i) {
        this((double) vector3i.x, (double) vector3i.y, (double) vector3i.z);
    }

    public Double distance(Double x2, Double y2, Double z2) {
        return Math.sqrt(Math.pow(x2 - x, 2) + Math.pow(y2 - y, 2) + Math.pow(z2 - z, 2));
    }

    public Double distance(Location location) {
        return distance(location.getX(), location.getY(), location.getZ());
    }

    public Double distance(Vec3 vec3) {
        return distance(vec3.x, vec3.y, vec3.z);
    }

    public Position withHeight(Double y) {
        return new Position(this.x, y, this.z);
    }

    public Location toLocation() {
        return new Location(x, y, z, 0f, 0f);
    }

    public Vector3d toVector3d() {
        return new Vector3d(x, y , z);
    }
}
