<template>
  <div class="dashboard-container" style="padding: 20px; background-color: #f5f7fa; min-height: 100vh;">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header><div class="card-header"><span>总收入 (已结算)</span></div></template>
          <div class="stat-value" style="color: #67c23a;">￥ {{ stats.revenue.toFixed(2) }}</div>
          <div class="stat-footer">累计财务收入</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header><div class="card-header"><span>药品种类</span></div></template>
          <div class="stat-value" style="color: #409eff;">{{ stats.medicineCount }}</div>
          <div class="stat-footer">在库药品品种</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header><div class="card-header"><span>病人总数</span></div></template>
          <div class="stat-value" style="color: #e6a23c;">{{ stats.patientCount }}</div>
          <div class="stat-footer">系统登记患者</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header><div class="card-header"><span>待处理账单</span></div></template>
          <div class="stat-value" style="color: #f56c6c;">{{ stats.pendingBills }}</div>
          <div class="stat-footer">等待缴费人数</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 左侧：库存预警 -->
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span style="font-weight: bold; color: #f56c6c;">⚠️ 药品库存预警 (低于20)</span>
            </div>
          </template>
          <el-table :data="lowStockMedicines" style="width: 100%" height="250">
            <el-table-column prop="name" label="药品名称" />
            <el-table-column prop="stock" label="剩余库存">
              <template #default="scope">
                <el-tag type="danger">{{ scope.row.stock }} {{ scope.row.unit }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 右侧：最近账单动态 -->
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header"><span style="font-weight: bold;">最近收支记录</span></div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="bill in recentBills"
              :key="bill.id"
              :type="bill.status === 1 ? 'success' : 'warning'"
              :timestamp="bill.createTime"
            >
              病人 ID: {{ bill.patientId }} - 
              <span :style="{ color: bill.status === 1 ? '#67c23a' : '#e6a23c' }">
                {{ bill.status === 1 ? '完成缴费' : '生成账单' }} (￥{{ bill.totalAmount }})
              </span>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { getMedicineList } from '@/api/medicine'
import { getBillList } from '@/api/bill'
import { getPatientList } from '@/api/patient'

const stats = reactive({
  revenue: 0,
  medicineCount: 0,
  patientCount: 0,
  pendingBills: 0
})

const lowStockMedicines = ref([])
const recentBills = ref([])

const loadDashboardData = async () => {
  try {
    // 1. 获取药品统计
    const medRes = await getMedicineList()
    stats.medicineCount = medRes.data.length
    lowStockMedicines.value = medRes.data.filter(m => m.stock < 20)

    // 2. 获取账单统计
    const billRes = await getBillList()
    const bills = billRes.data || []
    stats.pendingBills = bills.filter(b => b.status === 0).length
    stats.revenue = bills.filter(b => b.status === 1)
                         .reduce((sum, b) => sum + b.totalAmount, 0)
    recentBills.value = bills.slice(-5).reverse() // 取最近5条

    // 3. 获取病人统计
    const patientRes = await getPatientList()
    stats.patientCount = patientRes.data.length
    
  } catch (error) {
    console.error('统计数据加载失败', error)
  }
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
.stat-card {
  text-align: center;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  padding: 10px 0;
}
.stat-footer {
  font-size: 12px;
  color: #909399;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
