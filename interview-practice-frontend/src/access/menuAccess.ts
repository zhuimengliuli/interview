import { MenuDataItem } from "@umijs/route-utils";
import checkAccess from "@/access/checkAccess";

/**
 * 获取可访问的菜单
 * @param loginUser
 * @param menuItems
 */
const getAccessMenus = (
  loginUser: API.LoginUserVO,
  menuItems: MenuDataItem[],
) => {
  return menuItems.filter((item) => {
    if (!checkAccess(loginUser, item.access)) {
      return false;
    }
    if (item.children) {
      item.children = getAccessMenus(loginUser, item.children);
    }
    return true;
  });
};

export default getAccessMenus;
