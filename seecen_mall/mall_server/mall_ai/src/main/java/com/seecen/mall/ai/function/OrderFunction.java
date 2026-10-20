package com.seecen.mall.ai.function;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seecen.mall.ai.mapper.OrderInfoMapper;
import com.seecen.mall.common.entity.OmsOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

/**
 * AI 订单查询工具。
 *
 * 当用户输入订单号并询问订单状态时，AI 可以调用此工具查询订单数据。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderFunction {

    /** 支付状态码到中文描述的映射：0 待支付 / 1 已支付 / 2 已取消。 */
    private static final String[] PAY_STATUS_DESC = {"待支付", "已支付", "已取消"};

    private final OrderInfoMapper orderInfoMapper;

    /**
     * 根据订单号查询订单状态。
     *
     * @param orderNo 订单号
     * @return 订单状态查询结果
     */
    @Tool(description = "根据订单号查询订单状态，返回订单号、状态、金额、创建时间和支付时间")
    public OrderResult getOrderStatus(@ToolParam(description = "订单号") String orderNo) {
        log.info("AI tool getOrderStatus called, orderNo={}", orderNo);
        OmsOrder order = orderInfoMapper.selectOne(
                new LambdaQueryWrapper<OmsOrder>().eq(OmsOrder::getOrderNo, orderNo)
        );
        if (order == null) {
            return new OrderResult(orderNo, "未找到该订单", 0d, "", "");
        }
        Integer payStatus = order.getPayStatus();
        String status = (payStatus != null && payStatus >= 0 && payStatus < PAY_STATUS_DESC.length)
                ? PAY_STATUS_DESC[payStatus]
                : "未知";
        return new OrderResult(
                order.getOrderNo(),
                status,
                order.getTotalAmount() != null ? order.getTotalAmount().doubleValue() : 0d,
                order.getCreateTime() != null ? order.getCreateTime().toString() : "",
                order.getPayTime() != null ? order.getPayTime().toString() : ""
        );
    }

    /**
     * 返回给 AI 的订单状态数据结构。
     *
     * @param orderNo     订单号
     * @param status      订单状态
     * @param totalAmount 订单金额
     * @param createTime  创建时间
     * @param payTime     支付时间
     */
    public record OrderResult(String orderNo, String status, double totalAmount, String createTime, String payTime) {
    }
}
