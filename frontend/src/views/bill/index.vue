<template>
  <div class="app-container" style="padding: 20px;">
    <el-card shadow="never" class="mb-20">
      <div style="font-weight: bold; font-size: 16px; margin-bottom: 10px;">待处理收费账单</div>
    </el-card>

    <el-card shadow="never" style="margin-top: 20px;">
      <el-table :data="pageData" border stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="账单流水号" width="120" align="center" />
        <el-table-column prop="patientId" label="病人ID" width="100" align="center" />
        <el-table-column prop="totalAmount" label="应收金额" align="center" width="150">
          <template #default="scope">
            <span style="color: #f56c6c; font-weight: bold;">￥{{ scope.row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="生成时间" align="center" />
        <el-table-column prop="status" label="支付状态" align="center" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '已支付' : '待缴费' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="scope">
            <el-button 
              v-if="scope.row.status === 0"
              type="primary" 
              size="small" 
              @click="handlePay(scope.row)"
            >
              确认缴费
            </el-button>
            <span v-else style="color: #909399; font-size: 12px;">已结算完成</span>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top: 20px; display: flex; justify-content: flex-end;">
        <el-pagination
            v-model:current-page="pageNum"
            :page-size="10"
            :total="total"
            layout="total, prev, pager, next"
            @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBillList, payBill } from '@/api/bill'

const loading = ref(false)
const allTableData = ref([])
const pageData = ref([])
const total = ref(0)
const pageNum = ref(1)

// 获取账单列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getBillList()
    allTableData.value = res.data || []
    handlePageChange()
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 分页处理
const handlePageChange = () => {
  total.value = allTableData.value.length
  const start = (pageNum.value - 1) * 10
  pageData.value = allTableData.value.slice(start, start + 10)
}

// 确认缴费操作
const handlePay = (row) => {
  ElMessageBox.confirm(
    `确认病人(ID:${row.patientId}) 已线下支付金额 ￥${row.totalAmount} 吗？`,
    '缴费确认',
    {
      confirmButtonText: '已收到钱',
      cancelButtonText: '取消',
      type: 'success',
    }
  ).then(async () => {
    try {
      await payBill(row.id)
      ElMessage.success('账单结算成功！')
      getList() // 刷新列表
    } catch (error) {
      console.error(error)
    }
  })
}

onMounted(() => {
  getList()
})
</script>