package erogenousbeef.bigreactors.core.client.render;

import erogenousbeef.bigreactors.core.util.vecmath.Vector3d;
import erogenousbeef.bigreactors.core.util.vecmath.Vector3f;
import erogenousbeef.bigreactors.core.util.vecmath.Vertex;

public interface VertexTransform {

    void apply(Vertex vertex);

    void apply(Vector3d vec);

    void applyToNormal(Vector3f vec);

}
