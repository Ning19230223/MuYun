package net.ximatai.muyun.platform.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import net.ximatai.muyun.ability.IChildrenAbility;
import net.ximatai.muyun.ability.ITreeAbility;
import net.ximatai.muyun.ability.curd.std.IDataCheckAbility;
import net.ximatai.muyun.base.BaseBusinessTable;
import net.ximatai.muyun.core.database.MyTableWrapper;
import net.ximatai.muyun.core.exception.MyException;
import net.ximatai.muyun.database.builder.TableWrapper;
import net.ximatai.muyun.model.ChildTableInfo;
import net.ximatai.muyun.model.TreeNode;
import net.ximatai.muyun.platform.ScaffoldForPlatform;
import net.ximatai.muyun.platform.model.DictCategory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static net.ximatai.muyun.platform.PlatformConst.BASE_PATH;

@Path(BASE_PATH + "/dict")
public class DictCategoryController extends ScaffoldForPlatform implements ITreeAbility, IChildrenAbility, IDataCheckAbility {

    @Inject
    DictController dictController;

    @Inject
    BaseBusinessTable base;

    @Override
    public String getMainTable() {
        return "app_dictcategory";
    }

    @Override
    public TableWrapper getTableWrapper() {
        return new MyTableWrapper(this)
            .setPrimaryKey("id")
            .setInherit(base.getTableWrapper())
            .addColumn("v_name")
            .addColumn("v_remark");
    }

    @Override
    public List<ChildTableInfo> getChildren() {
        return List.of(
            dictController.toChildTable("id_at_app_dictcategory")
        );
    }

    @GET
    @Path("/tree/{id}")
    public List<TreeNode> tree(@PathParam("id") String id) {
        return dictController.tree(id, false, null, null);
    }

    @GET
    @Path("/translate/{category}")
    public String translate(@PathParam("category") String category, @QueryParam("source") String source) {

        TreeNode node = tree(category).stream().filter(treeNode -> treeNode.getData().get("v_value").equals(source)).findFirst().orElse(null);
        if (node == null) {
            throw new MyException("字典值 %s 在 %s 类型中不存在".formatted(source, category));
        }

        return node.getData().get("v_name").toString();
    }

    @Override
    public void check(Map body, boolean isUpdate) {
        String id = (String) Objects.requireNonNull(body.get("id"), "数据字典类目必须提供ID");

        if (!id.equals(id.toLowerCase())) {
            throw new MyException("数据字典类目ID不能包含大写字母");
        }
    }

    @Override
    public void onTableCreated(boolean isFirst) {
        List.of(
            new DictCategory("muyun_dir", "平台自用", 0),
            new DictCategory("platform_dir", "平台业务", 1)
        ).forEach(dictCategory -> {
            this.putDictCategory(dictCategory, true);
        });
    }

    public void putDictCategory(DictCategory dictCategory, boolean isLock) {
        Map<String, ?> category = null;
        try {
            category = this.view(dictCategory.getId());
        } catch (Exception ignored) {

        }

        if (category == null) {
            this.create(dictCategory.toMap());
        } else if (isLock) {
            this.update(dictCategory.getId(), dictCategory.toMap());
        }
    }
}