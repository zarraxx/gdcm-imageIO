package com.oritsh.imageIO.codec;

import org.w3c.dom.Node;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;

/**
 * Created by zarra on 14-10-4.
 */
public class GdcmMetaData extends IIOMetadata {
    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public Node getAsTree(String formatName) {
        return null;
    }

    @Override
    public void mergeTree(String formatName, Node root) throws IIOInvalidTreeException {

    }

    @Override
    public void reset() {

    }
}
