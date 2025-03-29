/**
 * API模块集合
 */
import * as auth from './auth'
import * as user from './user'
import * as home from './home'
import * as order from './order'
import * as courier from './courier'
import notice from './notice'
import * as search from './search'
import * as file from './file'
import * as payment from './payment'

// 导出所有API模块
export default {
  auth,
  user,
  home,
  order,
  courier,
  notice,
  search,
  file,
  payment
} 