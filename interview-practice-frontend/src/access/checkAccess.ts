import LoginUserVO = API.LoginUserVO;
import ACCESS_ENUM from "@/access/accessEnum";

const checkAccess = (
  loginUser: LoginUserVO,
  needAccess = ACCESS_ENUM.NOT_LOGIN,
) => {
  // 获取当前用户登录态（如果没有，默认未登录状态）
  const loginUserAccess = loginUser?.userRole ?? ACCESS_ENUM.NOT_LOGIN;
  // 如果不需要登录权限，直接返回
  if (needAccess === ACCESS_ENUM.NOT_LOGIN) {
    return true;
  }
  // 如果需要登录才能访问
  if (needAccess === ACCESS_ENUM.USER) {
    if (loginUserAccess === ACCESS_ENUM.NOT_LOGIN) {
      return false;
    }
  }
  // 如果需要管理员权限
  if (needAccess === ACCESS_ENUM.ADMIN) {
    if (loginUserAccess !== ACCESS_ENUM.ADMIN) {
      return false;
    }
  }
  return true;
};

export default checkAccess;
