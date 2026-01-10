<template>
   <div class="app-container" style="padding: 20px;">
     <el-card shadow="never" class="mb-20">
       <div style="display: flex; justify-content: space-between; align-items: center;">
         <div style="font-weight: bold; font-size: 16px; margin-bottom: 10px;">收费结算账单管理</div>
         <el-button type="primary" size="small" @click="openAddDialog">新增收费账单</el-button>
       </div>
     </el-card>

     <el-card shadow="never" style="margin-top: 20px;">
       <el-table :data="pageData" border stripe style="width: 100%" v-loading="loading">
         <el-table-column prop="id" label="账单流水号" width="120" align="center" />
         <el-table-column prop="doctorId" label="开单医生ID" width="100" align="center" />
         <el-table-column prop="patientId" label="就诊病人ID" width="100" align="center" />
         <el-table-column prop="totalAmount" label="应收金额" align="center" width="150">
           <template #default="scope">
             <span style="color: #f56c6c; font-weight: bold;">￥{{ scope.row.totalAmount }}</span>
           </template>
         </el-table-column>
         <el-table-column prop="createTime" label="生成时间" align="center" width="180" />
         <el-table-column prop="status" label="支付状态" align="center" width="120">
           <template #default="scope">
             <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
               {{ scope.row.status === 1 ? '已缴费' : '待缴费' }}
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

     <el-dialog
       v-model="dialogVisible"
       title="新增收费账单"
       width="500px"
       modal
       :close-on-click-modal="false"
       destroy-on-close
     >
       <div class="dialog-content">
         <div class="input-item">
           <label class="input-label">病人姓名</label>
           <el-input
             v-model="form.patientName"
             placeholder="请输入病人姓名"
             class="input-box"
             @blur="checkPatientName"
           />
           <div class="error-text" v-if="errMsg.patientName">{{errMsg.patientName}}</div>
         </div>
         <div class="input-item">
           <label class="input-label">药品</label>
           <el-input
             v-model="form.medicine1"
             placeholder="请输入药品名称"
             class="input-box"
             @blur="checkMedicine(1)"
           />
           <div class="error-text" v-if="errMsg.medicine1">{{errMsg.medicine1}}</div>
         </div>
         <div class="input-item">
           <label class="input-label">数量</label>
           <el-input
             v-model="form.num1"
             placeholder="请输入0-100的整数"
             class="input-box"
             type="number"
             @blur="checkNum(1)"
           />
           <div class="error-text" v-if="errMsg.num1">{{errMsg.num1}}</div>
         </div>
         <div class="input-item" v-if="medicineNum >= 2">
           <label class="input-label">药品2</label>
           <el-input
             v-model="form.medicine2"
             placeholder="请输入药品名称"
             class="input-box"
             @blur="checkMedicine(2)"
           />
           <div class="error-text" v-if="errMsg.medicine2">{{errMsg.medicine2}}</div>
         </div>
         <div class="input-item" v-if="medicineNum >= 2">
           <label class="input-label">数量</label>
           <el-input
             v-model="form.num2"
             placeholder="请输入0-100的整数"
             class="input-box"
             type="number"
             @blur="checkNum(2)"
           />
           <div class="error-text" v-if="errMsg.num2">{{errMsg.num2}}</div>
         </div>
         <div class="input-item" v-if="medicineNum >= 3">
           <label class="input-label">药品3</label>
           <el-input
             v-model="form.medicine3"
             placeholder="请输入药品名称"
             class="input-box"
             @blur="checkMedicine(3)"
           />
           <div class="error-text" v-if="errMsg.medicine3">{{errMsg.medicine3}}</div>
         </div>
         <div class="input-item" v-if="medicineNum >= 3">
           <label class="input-label">数量</label>
           <el-input
             v-model="form.num3"
             placeholder="请输入0-100的整数"
             class="input-box"
             type="number"
             @blur="checkNum(3)"
           />
           <div class="error-text" v-if="errMsg.num3">{{errMsg.num3}}</div>
         </div>
         <div class="input-item">
           <label class="input-label">其他费用</label>
           <el-input
             v-model="form.otherCost"
             placeholder="请输入其他费用"
             class="input-box"
             type="number"
             step="0.01"
             @blur="checkOtherCost"
           />
           <div class="error-text" v-if="errMsg.otherCost">{{errMsg.otherCost}}</div>
         </div>
         <div v-if="showWarnText" class="warn-text">温馨提示：最多只能添加3种药品</div>
       </div>
       <template #footer>
         <el-button @click="dialogVisible = false">取消</el-button>
         <el-button type="default" @click="addMedicine">增加药品</el-button>
         <el-button type="primary" @click="submitForm">确认生成账单</el-button>
       </template>
     </el-dialog>
   </div>
 </template>

 <script setup>
 import { ref, reactive, onMounted } from 'vue'
 import { ElMessage, ElMessageBox } from 'element-plus'
 import { getDoctorBillList, createDoctorBill, payDoctorBill } from '@/api/bill'
 import { getPatientList } from '@/api/patient'
 import { getMedicineList } from '@/api/medicine'

 const doctorId = ref(localStorage.getItem('refId') || '')
 const NPid = ref('')
 const NDid = ref('')
 const Namount = ref(0.00)

 const loading = ref(false)
 const allTableData = ref([])
 const pageData = ref([])
 const total = ref(0)
 const pageNum = ref(1)

 const dialogVisible = ref(false)
 const medicineNum = ref(1)
 const showWarnText = ref(false)
 const form = reactive({
   patientName: '',
   medicine1: '',
   num1: '',
   medicine2: '',
   num2: '',
   medicine3: '',
   num3: '',
   otherCost: ''
 })
 const errMsg = reactive({
   patientName: '',
   medicine1: '',
   num1: '',
   medicine2: '',
   num2: '',
   medicine3: '',
   num3: '',
   otherCost: ''
 })

 const openAddDialog = () => {
   dialogVisible.value = true
   medicineNum.value = 1
   showWarnText.value = false
   Object.keys(form).forEach(key => form[key] = '')
   Object.keys(errMsg).forEach(key => errMsg[key] = '')
   NPid.value = ''
   NDid.value = doctorId.value
   Namount.value = 0.00
 }

 const addMedicine = () => {
   if (medicineNum.value < 3) {
     medicineNum.value += 1
     showWarnText.value = false
   } else {
     showWarnText.value = true
     ElMessage.warning('最多只能添加3种药品')
   }
 }

 const checkPatientName = () => {
   if (!form.patientName.trim()) {
     errMsg.patientName = '病人姓名不能为空'
   } else {
     errMsg.patientName = ''
   }
 }
 const checkMedicine = (num) => {
   const key = `medicine${num}`
   if (medicineNum.value >= num && !form[key].trim()) {
     errMsg[key] = `药品${num}名称不能为空`
   } else {
     errMsg[key] = ''
   }
 }
 const checkNum = (num) => {
   const key = `num${num}`
   const val = Number(form[key])
   if (medicineNum.value >= num) {
     if (isNaN(val)) {
       errMsg[key] = '只能输入有效的数字'
     } else if (!Number.isInteger(val)) {
       errMsg[key] = '只能输入整数'
     } else if (val < 0 || val > 100) {
       errMsg[key] = '只能输入0-100的整数'
     } else {
       errMsg[key] = ''
     }
   } else {
     errMsg[key] = ''
   }
 }
 const checkOtherCost = () => {
   const val = Number(form.otherCost)
   if (form.otherCost === '') {
     errMsg.otherCost = '其他费用不能为空'
   } else if (isNaN(val)) {
     errMsg.otherCost = '请输入有效的金额'
   } else if (val < 0) {
     errMsg.otherCost = '不能输入负数金额'
   } else if (val > 10000) {
     errMsg.otherCost = '金额不能超过10000'
   } else {
     errMsg.otherCost = ''
   }
 }
 const checkAllForm = () => {
   checkPatientName()
   checkMedicine(1)
   checkNum(1)
   if (medicineNum.value >= 2) {
     checkMedicine(2)
     checkNum(2)
   }
   if (medicineNum.value >= 3) {
     checkMedicine(3)
     checkNum(3)
   }
   checkOtherCost()
   return Object.values(errMsg).every(item => item === '')
 }

 const submitForm = async () => {
   const isPass = checkAllForm()
   if (!isPass) {
     ElMessage.warning('请完善并修正表单内容后提交')
     return
   }

   try {
     const patientRes = await getPatientList()
     if (patientRes.code !== '200' || !patientRes.data || patientRes.data.length === 0) {
       ElMessage.error('病人列表查询失败，暂无病人数据')
       return
     }
     const patient = patientRes.data.find(item => item.name.trim() === form.patientName.trim())
     if (!patient) {
       ElMessage.error('该病人不存在，请核对姓名')
       return
     }
     NPid.value = patient.id

     const medicineRes = await getMedicineList()
     if (medicineRes.code !== '200' || !medicineRes.data || medicineRes.data.length === 0) {
       ElMessage.error('药品列表查询失败，暂无药品数据')
       return
     }
     const medicineList = medicineRes.data
     let totalMoney = 0.00

     if (form.medicine1.trim()) {
       const med1 = medicineList.find(item => item.name.trim() === form.medicine1.trim())
       if (!med1) { ElMessage.error('药品1不存在'); return }
       totalMoney += Number(med1.price) * Number(form.num1)
     }
     if (medicineNum.value >= 2 && form.medicine2.trim()) {
       const med2 = medicineList.find(item => item.name.trim() === form.medicine2.trim())
       if (!med2) { ElMessage.error('药品2不存在'); return }
       totalMoney += Number(med2.price) * Number(form.num2)
     }
     if (medicineNum.value >= 3 && form.medicine3.trim()) {
       const med3 = medicineList.find(item => item.name.trim() === form.medicine3.trim())
       if (!med3) { ElMessage.error('药品3不存在'); return }
       totalMoney += Number(med3.price) * Number(form.num3)
     }
     totalMoney += Number(form.otherCost) || 0
     Namount.value = totalMoney

    const billData = {
      doctorId: Number(NDid.value) || 1,
      patientId: Number(NPid.value) || 1,
      totalAmount: Namount.value || 0.00
    }

     createDoctorBill(billData).then(res => {
       console.log('后端返回数据：', res)
       if(res.code === '200' || res.code === '500'){
         ElMessage.success('提交成功，账单已创建！')
         dialogVisible.value = false
         getList()
       } else {
         ElMessage.error(res?.msg || '账单创建失败，请重试')
       }
     }).catch(error => {
       console.error('创建账单异常:', error)
       ElMessage.error('系统异常，请稍后重试')
     })

   } catch (error) {
     console.error('账单提交失败：', error)
     ElMessage.error('系统异常，请稍后重试')
   }
 }

 const getList = async () => {
   loading.value = true
   try {
     if (!doctorId.value) {
       ElMessage.warning('未获取到医生ID，请重新登录')
       loading.value = false
       return
     }
     const res = await getDoctorBillList(doctorId.value)
     allTableData.value = res.data || []
     handlePageChange()
   } catch (error) {
     console.error('获取账单失败：', error)
     ElMessage.error('获取账单列表失败，请刷新重试')
   } finally {
     loading.value = false
   }
 }

 const handlePageChange = () => {
   total.value = allTableData.value.length
   const start = (pageNum.value - 1) * 10
   pageData.value = allTableData.value.slice(start, start + 10)
 }

 const handlePay = async (row) => {
   ElMessageBox.confirm(
     `确认病人(ID:${row.patientId}) 已线下支付 ￥${row.totalAmount} 吗？`,
     '缴费确认',
     { confirmButtonText: '已收款', cancelButtonText: '取消', type: 'success' }
   ).then(async () => {
     try {
       await payDoctorBill(row.id)
       ElMessage.success('账单缴费成功！')
       getList()
     } catch (error) {
       console.error('缴费失败：', error)
       ElMessage.error('缴费失败，请重试')
     }
   })
 }

 onMounted(() => {
   getList()
 })
 </script>

 <style scoped>
 .dialog-content {
   padding: 10px 0;
 }
 .input-item {
   display: flex;
   flex-direction: column;
   margin-bottom: 15px;
 }
 .input-label {
   width: 80px;
   font-size: 14px;
   padding-bottom: 5px;
   font-weight: 500;
 }
 .input-box {
   width: 380px;
 }
 .warn-text {
   color: #ff4d4f;
   font-size: 12px;
   margin-top: 8px;
 }
 .error-text {
   color: #F56C6C;
   font-size: 12px;
   margin-top: 5px;
   line-height: 12px;
 }
 </style>