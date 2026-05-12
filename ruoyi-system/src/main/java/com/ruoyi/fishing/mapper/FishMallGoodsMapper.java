package com.ruoyi.fishing.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.fishing.domain.FishMallGoods;

public interface FishMallGoodsMapper
{
    FishMallGoods selectById(Long goodsId);
    List<FishMallGoods> selectList(FishMallGoods g);
    /** 上架且按分类查（小程序用）；catId 为 null 返回全部上架 */
    List<FishMallGoods> selectActive(@Param("catId") Long catId);
    int insert(FishMallGoods g);
    int update(FishMallGoods g);
    int updateStatus(@Param("goodsId") Long goodsId, @Param("status") String status);
    /** 原子扣库存：库存够才扣减 */
    int decreaseStock(@Param("goodsId") Long goodsId, @Param("qty") Integer qty);
    int increaseStock(@Param("goodsId") Long goodsId, @Param("qty") Integer qty);
    int increaseSales(@Param("goodsId") Long goodsId, @Param("qty") Integer qty);
    int deleteByIds(Long[] goodsIds);
}
