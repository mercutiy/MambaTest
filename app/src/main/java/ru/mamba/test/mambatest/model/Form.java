package ru.mamba.test.mambatest.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.mamba.test.mambatest.model.form.Block;

public class Form extends Model {

    private final static String F_LST_BLOCKS = "blocks";

    private Block[] mBlock;

    public Form(JSONObject json) throws JSONException {
        super(json);
        JSONArray jsonBlocks = json.getJSONArray(F_LST_BLOCKS);
        List<Block> blocks = new ArrayList<Block>();
        for (int i = 0; i < jsonBlocks.length(); i++) {
            blocks.add(new Block(jsonBlocks.getJSONObject(i)));
        }

        setBlock(blocks.toArray(new Block[blocks.size()]));
    }

    public Block[] getBlock() {
        return mBlock;
    }

    public void setBlock(Block[] block) {
        mBlock = block;
    }

    public JSONObject getJson() {
        return new JSONObject();
    }
}
