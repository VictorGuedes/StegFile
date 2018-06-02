namespace StegWebApi.Models
{
    using System;
    using System.Data.Entity;
    using System.ComponentModel.DataAnnotations.Schema;
    using System.Linq;

    public partial class BancoContext : DbContext
    {
        public BancoContext()
            : base("name=BancoContext")
        {
        }

        public virtual DbSet<Anexo> Anexo { get; set; }
        public virtual DbSet<Usuario> Usuario { get; set; }

        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Anexo>()
                .Property(e => e.caminhoArquivo)
                .IsUnicode(false);

            modelBuilder.Entity<Usuario>()
                .Property(e => e.username)
                .IsUnicode(false);

            modelBuilder.Entity<Usuario>()
                .Property(e => e.password)
                .IsUnicode(false);
        }
    }
}
