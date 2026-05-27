// heightValidator.js
import { Message } from 'element-ui'

/**
 * 身高验证函数
 * @param {number} age - 年龄
 * @param {number} height - 身高(cm)
 * @param {string} type - 类型 ('father', 'mother', 'child' 等)
 * @returns {boolean} 是否通过验证
 */
export const validateHeight = (age, height, type = 'adult') => {
    if (!height && height !== 0) return true // 空值不验证

    const heightNum = Number(height)

    if (isNaN(heightNum)) {
        Message.warning('请输入有效的身高数值')
        return false
    }

    // 成年人验证 (年龄 >= 18 或明确指定为成人)
    if (age >= 17 || type === 'father' || type === 'mother') {
        if (heightNum < 130) {
            Message.warning('成年人身高不应低于130cm，请确认输入是否正确')
            return false
        }
        if (heightNum > 190) {
            Message.warning('身高高于190cm，检查身高是否有误')
            return false
        }
    }
    // 儿童验证
    else {
        if (heightNum < 40) {
            Message.warning('儿童身高不应低于40cm，请确认输入是否正确')
            return false
        }
        if (heightNum > 190) {
            Message.warning('身高高于190cm，检查身高是否有误')
            return false
        }
    }

    return true
}
/**
 * 根据出生日期计算年龄
 * @param {string} birthdate - 出生日期，格式为 "1969-12-29"
 * @returns {number} 年龄
 */
const calculateAge = (birthdate) => {
    if (!birthdate) return 0
    
    const birthDate = new Date(birthdate)
    const today = new Date()
    
    // 检查日期是否有效
    if (isNaN(birthDate.getTime())) {
        console.warn('无效的出生日期格式:', birthdate)
        return 0
    }
    
    let age = today.getFullYear() - birthDate.getFullYear()
    const monthDiff = today.getMonth() - birthDate.getMonth()
    
    // 如果当前月份小于出生月份，或者月份相同但当前日期小于出生日期，则年龄减1
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
        age--
    }
    
    return age
}
/**

 * 根据出生日期验证身高
 * @param {string} birthdate - 出生日期，格式为 "1969-12-29"
 * @param {number} height - 身高(cm)
 * @param {string} type - 类型 ('father', 'mother', 'child' 等)
 * @returns {boolean} 是否通过验证
 */
export const validateHeightByBirthdate = (birthdate, height, type = 'adult') => {
    if (!height && height !== 0) return true // 空值不验证

    const heightNum = Number(height)

    if (isNaN(heightNum)) {
        Message.warning('请输入有效的身高数值')
        return false
    }

    // 计算年龄
    const age = calculateAge(birthdate)
    
    // 成年人验证 (年龄 >= 18 或明确指定为成人)
    if (age >= 17 || type === 'father' || type === 'mother') {
        if (heightNum < 130) {
            Message.warning('成年人身高不应低于130cm，请确认输入是否正确')
            return false
        }
        if (heightNum > 190) {
            Message.warning('身高高于190cm，检查身高是否有误')
            return false
        }
    }
    // 儿童验证
    else {
        if (heightNum < 40) {
            Message.warning('儿童身高不应低于40cm，请确认输入是否正确')
            return false
        }
        if (heightNum > 190) {
            Message.warning('身高高于190cm，检查身高是否有误')
            return false
        }
    }

    return true
}
/**
 * 身高年龄验证函数
 * @param {number} age - 年龄
 * @param {number} height - 身高(cm)
 * @param {string} type - 类型 ('father', 'mother', 'child' 等)
 * @returns {boolean} 是否通过验证
 */
export const validateAgeHeight = (age, height) => {
    if (!height && height !== 0) return true // 空值不验证

    const heightNum = Number(height)

    if (isNaN(heightNum)) {
        Message.warning('请输入有效的身高数值')
        return false
    }

    // 成年人验证 (年龄 >= 18 或明确指定为成人)
    if (age >= 17) {
        if (heightNum < 130) {
            Message.warning('成年人身高不应低于130cm，请确认输入是否正确')
            return false
        }
        if (heightNum > 190) {
            Message.warning('身高高于190cm，检查身高是否有误')
            return false
        }
    }
    // 儿童验证
    else {
        if (heightNum < 40) {
            Message.warning('儿童身高不应低于40cm，请确认输入是否正确')
            return false
        }
        if (heightNum > 190) {
            Message.warning('身高高于190cm，检查身高是否有误')
            return false
        }
    }
    return true
}