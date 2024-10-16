public abstract class HttpServlet {
   protected void doGet(HttpServletRequest request, HttpServletResponse response) {
      System.out.println("HttpServlet: doGet called");
   }

   protected void doPost(HttpServletRequest request, HttpServletResponse response) {
      System.out.println("HttpServlet: doPost called");
   }
}
