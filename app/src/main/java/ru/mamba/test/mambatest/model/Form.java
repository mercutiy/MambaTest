package ru.mamba.test.mambatest.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.mamba.test.mambatest.model.form.Block;
import ru.mamba.test.mambatest.model.form.Field;

public class Form extends Model {

    private final static String F_LST_BLOCKS = "blocks";

    private Block[] mBlocks;

    public Form(JSONObject json) throws JSONException {
        super(json);
        JSONArray jsonBlocks = json.getJSONArray(F_LST_BLOCKS);
        List<Block> blocks = new ArrayList<Block>();
        for (int i = 0; i < jsonBlocks.length(); i++) {
            blocks.add(new Block(jsonBlocks.getJSONObject(i)));
        }

        setBlocks(blocks.toArray(new Block[blocks.size()]));
    }

    public Block[] getBlocks() {
        return mBlocks;
    }

    public void setBlocks(Block[] block) {
        mBlocks = block;
    }

    public JSONObject getJson() {
        JSONObject response = new JSONObject();

        for (Block block : getBlocks()) {
            try {
                JSONObject jsonBlock = new JSONObject();
                for (Field field : block.getFields()) {
                    field.actualize();
                    jsonBlock.put(field.getField(), field.getValue());
                }
                response.put(block.getField(), jsonBlock);
            } catch (JSONException e) {
                // todo сделать что-то
            }
        }

        return response;
    }
}
